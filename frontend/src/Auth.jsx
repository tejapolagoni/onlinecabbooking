import { useState } from "react";
import api, { errMsg } from "./api";

export default function Auth({ onLogin }) {
  const [isLogin, setIsLogin] = useState(true);
  const [f, setF] = useState({ name: "", email: "", password: "", phone: "", role: "USER", vehicleNumber: "", vehicleType: "", adminSetupKey: "" });
  const [msg, setMsg] = useState("");
  const set = (k) => (e) => setF({ ...f, [k]: e.target.value });

  const submit = async () => {
    try {
      if (isLogin) {
        const { data } = await api.post("/auth/login", { email: f.email, password: f.password });
        onLogin(data);
      } else {
        const { adminSetupKey, ...registration } = f;
        const config = f.role === "ADMIN"
          ? { headers: { "X-Admin-Setup-Key": adminSetupKey } }
          : {};
        await api.post("/auth/register", registration, config);
        setMsg("Registered! Please login.");
        setIsLogin(true);
      }
    } catch (e) { setMsg(errMsg(e)); }
  };

  return (
    <div className="card auth">
      <h2>🚖 Cab Booking</h2>
      <h3>{isLogin ? "Login" : "Register"}</h3>
      {!isLogin && <>
        <input placeholder="Name" value={f.name} onChange={set("name")} />
        <input placeholder="Phone" value={f.phone} onChange={set("phone")} />
        <select value={f.role} onChange={set("role")}>
          <option value="USER">Passenger</option>
          <option value="DRIVER">Driver</option>
          <option value="ADMIN">Admin</option>
        </select>
        {f.role === "DRIVER" && <>
          <input placeholder="Vehicle Number" value={f.vehicleNumber} onChange={set("vehicleNumber")} />
          <input placeholder="Vehicle Type (Auto/Mini/Sedan)" value={f.vehicleType} onChange={set("vehicleType")} />
        </>}
        {f.role === "ADMIN" && <>
          <input type="password" placeholder="Admin setup key" value={f.adminSetupKey} onChange={set("adminSetupKey")} />
          <small>Admin registration requires the setup key configured by the server.</small>
        </>}
      </>}
      <input placeholder="Email" value={f.email} onChange={set("email")} />
      <input type="password" placeholder="Password" value={f.password} onChange={set("password")} />
      <button onClick={submit}>{isLogin ? "Login" : "Register"}</button>
      <p className="msg">{msg}</p>
      <a href="#" onClick={(e) => { e.preventDefault(); setIsLogin(!isLogin); setMsg(""); }}>
        {isLogin ? "New here? Register" : "Have an account? Login"}
      </a>
    </div>
  );
}
