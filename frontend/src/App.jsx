import { useState } from "react";
import Auth from "./Auth";
import { UserDashboard, DriverDashboard, AdminDashboard } from "./Dashboards";
import "./styles.css";

export default function App() {
  const [user, setUser] = useState(() => JSON.parse(localStorage.getItem("user") || "null"));
  const login = (u) => { localStorage.setItem("user", JSON.stringify(u)); setUser(u); };
  const logout = () => { localStorage.removeItem("user"); setUser(null); };

  if (!user) return <Auth onLogin={login} />;
  return (
    <div className="container">
      <header>
        <h2>🚖 Cab Booking</h2>
        <span>{user.name} ({user.role}) <button onClick={logout}>Logout</button></span>
      </header>
      {user.role === "USER" && <UserDashboard user={user} />}
      {user.role === "DRIVER" && <DriverDashboard user={user} />}
      {user.role === "ADMIN" && <AdminDashboard />}
    </div>
  );
}
