// visual-check.js
// Kullanım örnekleri:
// 1) Baseline oluştur (ilk koşu): node visual-check.js --url=https://example.com
// 2) Karşılaştır: node visual-check.js --url=https://example.com
// Opsiyonlar:
//   --url=...                (zorunlu)
//   --out=./__shots__        (varsayılan çıktı klasörü)
//   --name=home              (dosya adı gövdesi: baseline/home.png vs current/home.png)
//   --fullPage=true|false    (varsayılan: true)
//   --width=1280 --height=800 (viewport; varsayılan 1280x800)
//   --wait=networkidle0|load|domcontentloaded (varsayılan: networkidle0)
//   --waitSelector=.ready    (opsiyonel: görününce devam eder)
//   --failText="Error"       (opsiyonel: sayfa metninde geçerse hata verir)
//   --threshold=0.01         (piksel eşik oranı; varsayılan %1)

import puppeteer from "puppeteer";
import fs from "fs";
import path from "path";
import { fileURLToPath } from "url";
import pixelmatch from "pixelmatch";
import { PNG } from "pngjs";

// --- basit arg parse ---
const args = Object.fromEntries(
  process.argv.slice(2).map(p => {
    const [k, ...rest] = p.replace(/^--/, "").split("=");
    return [k, rest.join("=") === "" ? true : rest.join("=")];
  })
);

if (!args.url) {
  console.error("HATA: --url parametresi zorunludur.");
  process.exit(2);
}

const OUT_DIR = args.out || "./__shots__";
const NAME = (args.name || "page").replace(/[^a-z0-9_-]/gi, "_");
const FULL_PAGE = String(args.fullPage ?? "true").toLowerCase() === "true";
const WIDTH = parseInt(args.width || "1280", 10);
const HEIGHT = parseInt(args.height || "800", 10);
const WAIT = args.wait || "networkidle0";
const WAIT_SELECTOR = args.waitSelector || null;
const FAIL_TEXT = args.failText || null;
const THRESHOLD = parseFloat(args.threshold || "0.01"); // %1

const __filename = fileURLToPath(import.meta.url);
const __dirname = path.dirname(__filename);

const BASELINE_DIR = path.join(__dirname, OUT_DIR, "baseline");
const CURRENT_DIR  = path.join(__dirname, OUT_DIR, "current");
const DIFF_DIR     = path.join(__dirname, OUT_DIR, "diff");

const BASELINE_PATH = path.join(BASELINE_DIR, `${NAME}.png`);
const CURRENT_PATH  = path.join(CURRENT_DIR,  `${NAME}.png`);
const DIFF_PATH     = path.join(DIFF_DIR,     `${NAME}.png`);

for (const d of [BASELINE_DIR, CURRENT_DIR, DIFF_DIR]) {
  fs.mkdirSync(d, { recursive: true });
}

function waitUntilOpt(page, wait) {
  switch (wait) {
    case "load": return page.waitForNavigation({ waitUntil: "load" });
    case "domcontentloaded": return page.waitForNavigation({ waitUntil: "domcontentloaded" });
    default: return page.waitForNavigation({ waitUntil: "networkidle0" });
  }
}

function readPng(p) {
  return new Promise((res, rej) => {
    fs.createReadStream(p)
      .pipe(new PNG())
      .on("parsed", function () { res(this); })
      .on("error", rej);
  });
}

(async () => {
  const browser = await puppeteer.launch();
  const page = await browser.newPage();
  await page.setViewport({ width: WIDTH, height: HEIGHT });

  // Sayfaya git
  const nav = page.goto(args.url, { waitUntil: "domcontentloaded" });
  await nav; // ilk ulaşma
  // Seçilen bekleme türü
  await new Promise(r => setTimeout(r, 100));
  if (WAIT) {
    // bazı durumlarda ikinci waitUntil daha stabil görüntü verir
    await page.waitForNavigation({ waitUntil: WAIT }).catch(() => {});
  }
  if (WAIT_SELECTOR) {
    await page.waitForSelector(WAIT_SELECTOR, { timeout: 15000 }).catch(() => {});
  }

  // Hata metni kontrolü (opsiyonel)
  if (FAIL_TEXT) {
    const content = await page.content();
    if (content.toLowerCase().includes(String(FAIL_TEXT).toLowerCase())) {
      console.error(`HATA: Sayfa içeriğinde '${FAIL_TEXT}' metni bulundu.`);
      await page.screenshot({ path: CURRENT_PATH, fullPage: FULL_PAGE });
      await browser.close();
      process.exit(3);
    }
  }

  // Güncel screenshot
  await page.screenshot({ path: CURRENT_PATH, fullPage: FULL_PAGE });

  // Baseline yoksa oluştur ve çıkar
  if (!fs.existsSync(BASELINE_PATH)) {
    fs.copyFileSync(CURRENT_PATH, BASELINE_PATH);
    console.log(`Baseline oluşturuldu: ${path.relative(process.cwd(), BASELINE_PATH)}`);
    await browser.close();
    process.exit(0);
  }

  // Karşılaştır
  const [img1, img2] = await Promise.all([readPng(BASELINE_PATH), readPng(CURRENT_PATH)]);

  // Boyut farklıysa canvasta hizalamak için genişlik/yükseklik min’ini al
  const width = Math.min(img1.width, img2.width);
  const height = Math.min(img1.height, img2.height);

  const diff = new PNG({ width, height });

  const diffPixels = pixelmatch(
    img1.data, img2.data, diff.data, width, height,
    { threshold: 0.1 } // eşik: anti-alias toleransı
  );

  // Diff kaydet
  diff.pack().pipe(fs.createWriteStream(DIFF_PATH));

  const total = width * height;
  const ratio = diffPixels / total;
  const percent = (ratio * 100).toFixed(3);

  console.log(`Karşılaştırma tamam: fark pikseli = ${diffPixels}/${total} (${percent}%)`);
  console.log(`Baseline: ${path.relative(process.cwd(), BASELINE_PATH)}`);
  console.log(`Current : ${path.relative(process.cwd(), CURRENT_PATH)}`);
  console.log(`Diff    : ${path.relative(process.cwd(), DIFF_PATH)}`);

  await browser.close();

  if (ratio > THRESHOLD) {
    console.error(`EŞİK AŞILDI: ${percent}% > ${(THRESHOLD * 100).toFixed(2)}%`);
    process.exit(1); // CI'da failure
  } else {
    console.log(`BAŞARILI: Fark ${percent}% (eşik ${(THRESHOLD * 100).toFixed(2)}%) altında.`);
    process.exit(0);
  }
})().catch(err => {
  console.error(err);
  process.exit(2);
});
