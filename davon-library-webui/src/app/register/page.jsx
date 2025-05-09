export default function RegisterPage() {
    return (
      <main>
        <h1>User Registration</h1>
        <form>
          <label>
            Name: <input type="text" name="name" />
          </label><br />
          <label>
            Email: <input type="email" name="email" />
          </label><br />
          <label>
            Password: <input type="password" name="password" />
          </label><br />
          <button type="submit">Register</button>
        </form>
      </main>
    );
  }
  