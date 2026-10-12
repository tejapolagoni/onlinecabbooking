import { useEffect, useState } from "react";
import api, { errMsg } from "./api";

function Table({ rows, actions }) {
  if (!rows.length) return <p>No bookings.</p>;
  return (
    <table>
      <thead><tr><th>ID</th><th>Pickup</th><th>Drop</th><th>Km</th><th>Fare ₹</th><th>Status</th><th>Driver</th><th>Action</th></tr></thead>
      <tbody>
        {rows.map((b) => (
          <tr key={b.id}>
            <td>{b.id}</td><td>{b.pickup}</td><td>{b.dropLocation}</td><td>{b.distanceKm}</td>
            <td>{b.fare}</td><td><span className={"tag " + b.status}>{b.status}</span></td>
            <td>{b.driverId ?? "-"}</td><td>{actions ? actions(b) : null}</td>
          </tr>
        ))}
      </tbody>
    </table>
  );
}

// helper: load a list from the API and expose a reload function
function useList(url) {
  const [rows, setRows] = useState([]);
  const load = () => api.get(url).then((r) => setRows(r.data));
  useEffect(() => { load(); }, [url]);
  return [rows, load];
}

async function act(method, url, reload, setMsg) {
  try { await api[method](url); setMsg(""); reload(); } catch (e) { setMsg(errMsg(e)); }
}

export function UserDashboard({ user }) {
  const [rows, load] = useList(`/bookings/user/${user.id}`);
  const [pickup, setPickup] = useState("");
  const [drop, setDrop] = useState("");
  const [msg, setMsg] = useState("");

  const book = async () => {
    if (!pickup || !drop) return setMsg("Enter pickup and drop");
    try {
      await api.post("/bookings", { userId: user.id, pickup, dropLocation: drop });
      setPickup(""); setDrop(""); setMsg(""); load();
    } catch (e) { setMsg(errMsg(e)); }
  };

  return (
    <>
      <div className="card">
        <h3>Book a Cab</h3>
        <input placeholder="Pickup location" value={pickup} onChange={(e) => setPickup(e.target.value)} />
        <input placeholder="Drop location" value={drop} onChange={(e) => setDrop(e.target.value)} />
        <button onClick={book}>Book Now</button>
        <p className="msg">{msg}</p>
      </div>
      <div className="card">
        <h3>My Bookings</h3>
        <Table rows={rows} actions={(b) =>
          (b.status === "PENDING" || b.status === "ACCEPTED") &&
          <button onClick={() => act("put", `/bookings/${b.id}/cancel`, load, setMsg)}>Cancel</button>} />
      </div>
    </>
  );
}

export function DriverDashboard({ user }) {
  const [pending, loadP] = useList("/bookings/pending");
  const [mine, loadM] = useList(`/bookings/driver/${user.id}`);
  const [msg, setMsg] = useState("");
  const reload = () => { loadP(); loadM(); };

  return (
    <>
      <p>Vehicle: {user.vehicleType} - {user.vehicleNumber}</p>
      <div className="card">
        <h3>New Ride Requests</h3>
        <p className="msg">{msg}</p>
        <Table rows={pending} actions={(b) =>
          <button onClick={() => act("put", `/bookings/${b.id}/accept?driverId=${user.id}`, reload, setMsg)}>Accept</button>} />
      </div>
      <div className="card">
        <h3>My Rides</h3>
        <Table rows={mine} actions={(b) =>
          b.status === "ACCEPTED" &&
          <button onClick={() => act("put", `/bookings/${b.id}/complete`, reload, setMsg)}>Complete</button>} />
      </div>
    </>
  );
}

export function AdminDashboard() {
  const [rows] = useList("/bookings");
  const [stats, setStats] = useState(null);
  const [statsError, setStatsError] = useState("");

  useEffect(() => {
    api.get("/admin/stats")
      .then((response) => setStats(response.data))
      .catch((error) => setStatsError(errMsg(error)));
  }, []);

  return (
    <>
      <div className="admin-stats">
        {[
          ["Drivers", stats?.drivers],
          ["Accepted Orders", stats?.acceptedBookings],
          ["Cancelled Orders", stats?.cancelledBookings],
          ["Completed Orders", stats?.completedBookings],
        ].map(([label, value]) => (
          <div className="card stat-card" key={label}>
            <h3>{label}</h3>
            <p className="stat-value">{value ?? "—"}</p>
          </div>
        ))}
      </div>
      {statsError && <p className="msg">{statsError}</p>}
      <div className="card">
        <h3>All Bookings</h3>
        <Table rows={rows} />
      </div>
    </>
  );
}
