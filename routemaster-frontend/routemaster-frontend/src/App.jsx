import React, { useState } from "react";
import {
  Container,
  Row,
  Col,
  Table,
  Card,
  Button,
  Form,
  Alert,
  Modal,
} from "react-bootstrap";
import {
  Menu,
  LogOut,
  Package,
  Truck,
  Users,
  BarChart3,
  Plus,
  Edit,
  Trash2,
} from "lucide-react";
import "bootstrap/dist/css/bootstrap.min.css";
import "./index.css";

function RouteMasterFrontend() {
  const [authMode, setAuthMode] = useState("login");
  const [currentPage, setCurrentPage] = useState("dashboard");
  const [isLoggedIn, setIsLoggedIn] = useState(false);
  const [sidebarOpen, setSidebarOpen] = useState(true);
  const [token, setToken] = useState(null);

  // Auth states
  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");
  const [firstName, setFirstName] = useState("");
  const [lastName, setLastName] = useState("");
  const [phone, setPhone] = useState("");
  const [address, setAddress] = useState("");

  // Data states
  const [parcels, setParcels] = useState([]);
  const [customers, setCustomers] = useState([]);
  const [routes, setRoutes] = useState([]);
  const [employees, setEmployees] = useState([]);
  const [dashboardStats, setDashboardStats] = useState({
    totalCustomers: 0,
    totalParcels: 0,
    totalPayment: 0,
    parcelsByRoute: {},
  });

  // Modal and form states - CUSTOMERS
  const [showCustomerModal, setShowCustomerModal] = useState(false);
  const [customerMode, setCustomerMode] = useState("add");
  const [customerForm, setCustomerForm] = useState({
    customerId: null,
    firstName: "",
    lastName: "",
    email: "",
    phone: "",
    address: "",
    password: "",
  });

  // Modal and form states - PARCELS
  const [showParcelModal, setShowParcelModal] = useState(false);
  const [parcelMode, setParcelMode] = useState("add");
  const [parcelForm, setParcelForm] = useState({
    parcelId: null,
    senderName: "",
    destinationAddress: "",
    destinationPincode: "",
    receiverName: "",
    parcelStatus: "PENDING",
    createdDate: new Date().toISOString().split("T")[0],
    customerId: "",
    routeId: "",
    totalPayment: "",
  });

  // Modal and form states - ROUTES
  const [showRouteModal, setShowRouteModal] = useState(false);
  const [routeMode, setRouteMode] = useState("add");
  const [routeForm, setRouteForm] = useState({
    routeId: null,
    name: "",
    description: "",
    pincode: "",
    totalDistance: "",
  });

  // Modal and form states - EMPLOYEES
  const [showEmployeeModal, setShowEmployeeModal] = useState(false);
  const [employeeMode, setEmployeeMode] = useState("add");
  const [employeeForm, setEmployeeForm] = useState({
    empId: null,
    firstName: "",
    lastName: "",
    email: "",
    phone: "",
    role: "",
    department: "",
    managerName: "",
  });

  // States
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState("");
  const [successMsg, setSuccessMsg] = useState("");

  const API_BASE = "http://localhost:8081";

  const showMessage = (message, isError = false) => {
    if (isError) {
      setError(message);
      setTimeout(() => setError(""), 3000);
    } else {
      setSuccessMsg(message);
      setTimeout(() => setSuccessMsg(""), 3000);
    }
  };

  // ===== AUTH FUNCTIONS =====
  const handleLogin = async () => {
    if (!email || !password) {
      showMessage("Please enter email and password", true);
      return;
    }

    setLoading(true);
    try {
      const response = await fetch(`${API_BASE}/api/auth/login`, {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ email, password }),
      });
      const data = await response.json();
      if (data.token) {
        setToken(data.token);
        setIsLoggedIn(true);
        setEmail("");
        setPassword("");
        setCurrentPage("dashboard");
        await loadDashboard(data.token);
        showMessage("Login successful!", false);
      } else {
        showMessage(data.error || "Login failed", true);
      }
    } catch (error) {
      showMessage("Login failed: " + error.message, true);
    }
    setLoading(false);
  };

  const handleRegister = async () => {
    if (!email || !password || !firstName || !lastName || !phone || !address) {
      showMessage("Please fill in all fields", true);
      return;
    }

    setLoading(true);
    try {
      const response = await fetch(`${API_BASE}/api/auth/register`, {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({
          email,
          password,
          firstName,
          lastName,
          phone,
          address,
        }),
      });
      const text = await response.text();

      if (text.includes("successfully")) {
        showMessage("Registration successful! Please login.", false);
        setAuthMode("login");
        setEmail("");
        setPassword("");
        setFirstName("");
        setLastName("");
        setPhone("");
        setAddress("");
      } else {
        showMessage(text || "Registration failed", true);
      }
    } catch (error) {
      showMessage("Registration failed: " + error.message, true);
    }
    setLoading(false);
  };

  const handleLogout = () => {
    setIsLoggedIn(false);
    setToken(null);
    setCurrentPage("dashboard");
    setParcels([]);
    setCustomers([]);
    setRoutes([]);
    setEmployees([]);
    setAuthMode("login");
    showMessage("Logged out successfully", false);
  };

  // ===== DATA LOADING FUNCTIONS =====
  const loadDashboard = async (authToken) => {
    try {
      const [customersRes, parcelsRes, paymentRes, routesRes] =
        await Promise.all([
          fetch(`${API_BASE}/dashboard/customercount`),
          fetch(`${API_BASE}/dashboard/parcelscount`),
          fetch(`${API_BASE}/dashboard/parcelpayment`),
          fetch(`${API_BASE}/dashboard/parcelroutecount`),
        ]);

      const customers = await customersRes.json();
      const parcels = await parcelsRes.json();
      const payment = await paymentRes.json();
      const routes = await routesRes.json();

      setDashboardStats({
        totalCustomers: customers,
        totalParcels: parcels,
        totalPayment: payment,
        parcelsByRoute: routes,
      });
    } catch (error) {
      console.error("Dashboard load error:", error);
    }
  };

  const loadParcels = async () => {
    if (!token) return;
    try {
      const response = await fetch(
        `${API_BASE}/api/parcels?pageNumber=0&size=20`,
        {
          headers: { Authorization: `Bearer ${token}` },
        }
      );
      const data = await response.json();
      setParcels(data.content || []);
    } catch (error) {
      showMessage("Failed to load parcels", true);
    }
  };

  const loadCustomers = async () => {
    if (!token) return;
    try {
      const response = await fetch(
        `${API_BASE}/api/customers?pageNumber=0&size=20`,
        {
          headers: { Authorization: `Bearer ${token}` },
        }
      );
      const data = await response.json();
      setCustomers(data.content || []);
    } catch (error) {
      showMessage("Failed to load customers", true);
    }
  };

  const loadRoutes = async () => {
    if (!token) return;
    try {
      const response = await fetch(
        `${API_BASE}/api/routes?pageNumber=0&size=20`,
        {
          headers: { Authorization: `Bearer ${token}` },
        }
      );
      const data = await response.json();
      setRoutes(data.content || []);
    } catch (error) {
      showMessage("Failed to load routes", true);
    }
  };

  const loadEmployees = async () => {
    if (!token) return;
    try {
      const response = await fetch(
        `${API_BASE}/api/employees?pageNumber=0&size=20`,
        {
          headers: { Authorization: `Bearer ${token}` },
        }
      );
      const data = await response.json();
      setEmployees(data.content || []);
    } catch (error) {
      showMessage("Failed to load employees", true);
    }
  };

  // ===== CUSTOMER CRUD OPERATIONS =====
  const openCustomerModal = (customer = null) => {
    if (customer) {
      setCustomerMode("edit");
      setCustomerForm(customer);
    } else {
      setCustomerMode("add");
      setCustomerForm({
        customerId: null,
        firstName: "",
        lastName: "",
        email: "",
        phone: "",
        address: "",
        password: "",
      });
    }
    setShowCustomerModal(true);
  };

  const handleSaveCustomer = async () => {
    if (
      !customerForm.firstName ||
      !customerForm.lastName ||
      !customerForm.email ||
      !customerForm.phone ||
      !customerForm.address
    ) {
      showMessage("Please fill in all required fields", true);
      return;
    }

    setLoading(true);
    try {
      const method = customerMode === "add" ? "POST" : "PUT";
      const endpoint =
        customerMode === "add"
          ? "/api/customers/create"
          : "/api/customers/update";

      const response = await fetch(`${API_BASE}${endpoint}`, {
        method: method,
        headers: {
          "Content-Type": "application/json",
          Authorization: `Bearer ${token}`,
        },
        body: JSON.stringify(customerForm),
      });

      const result = await response.json();
      if (result.message || result.customerId) {
        showMessage(
          customerMode === "add"
            ? "Customer created successfully"
            : "Customer updated successfully",
          false
        );
        setShowCustomerModal(false);
        await loadCustomers();
      } else {
        showMessage(result.error || "Failed to save customer", true);
      }
    } catch (error) {
      showMessage("Error saving customer: " + error.message, true);
    }
    setLoading(false);
  };

  const handleDeleteCustomer = async (customerId) => {
    if (!window.confirm("Are you sure you want to delete this customer?"))
      return;

    setLoading(true);
    try {
      const response = await fetch(`${API_BASE}/api/customers/delete`, {
        method: "DELETE",
        headers: {
          "Content-Type": "application/json",
          Authorization: `Bearer ${token}`,
        },
        body: JSON.stringify({ customerId }),
      });

      const result = await response.json();
      if (result.message) {
        showMessage("Customer deleted successfully", false);
        await loadCustomers();
      } else {
        showMessage(result.error || "Failed to delete customer", true);
      }
    } catch (error) {
      showMessage("Error deleting customer: " + error.message, true);
    }
    setLoading(false);
  };

  // ===== PARCEL CRUD OPERATIONS =====
  const openParcelModal = (parcel = null) => {
    if (parcel) {
      setParcelMode("edit");
      setParcelForm(parcel);
    } else {
      setParcelMode("add");
      setParcelForm({
        parcelId: null,
        senderName: "",
        destinationAddress: "",
        destinationPincode: "",
        receiverName: "",
        parcelStatus: "PENDING",
        createdDate: new Date().toISOString().split("T")[0],
        customerId: "",
        routeId: "",
        totalPayment: "",
      });
    }
    setShowParcelModal(true);
  };

  const handleSaveParcel = async () => {
    if (
      !parcelForm.senderName ||
      !parcelForm.destinationAddress ||
      !parcelForm.receiverName ||
      !parcelForm.customerId ||
      !parcelForm.routeId
    ) {
      showMessage("Please fill in all required fields", true);
      return;
    }

    setLoading(true);
    try {
      const method = parcelMode === "add" ? "POST" : "PUT";
      const endpoint =
        parcelMode === "add" ? "/api/parcels/create" : "/api/parcels/update";

      const response = await fetch(`${API_BASE}${endpoint}`, {
        method: method,
        headers: {
          "Content-Type": "application/json",
          Authorization: `Bearer ${token}`,
        },
        body: JSON.stringify(parcelForm),
      });

      const result = await response.json();
      if (result.message || result.parcelId) {
        showMessage(
          parcelMode === "add"
            ? "Parcel created successfully"
            : "Parcel updated successfully",
          false
        );
        setShowParcelModal(false);
        await loadParcels();
      } else {
        showMessage(result.error || "Failed to save parcel", true);
      }
    } catch (error) {
      showMessage("Error saving parcel: " + error.message, true);
    }
    setLoading(false);
  };

  const handleDeleteParcel = async (parcelId) => {
    if (!window.confirm("Are you sure you want to delete this parcel?")) return;

    setLoading(true);
    try {
      const response = await fetch(`${API_BASE}/api/parcels/delete`, {
        method: "DELETE",
        headers: {
          "Content-Type": "application/json",
          Authorization: `Bearer ${token}`,
        },
        body: JSON.stringify({ parcelId }),
      });

      const result = await response.json();
      if (result.message) {
        showMessage("Parcel deleted successfully", false);
        await loadParcels();
      } else {
        showMessage(result.error || "Failed to delete parcel", true);
      }
    } catch (error) {
      showMessage("Error deleting parcel: " + error.message, true);
    }
    setLoading(false);
  };

  // ===== ROUTE CRUD OPERATIONS =====
  const openRouteModal = (route = null) => {
    if (route) {
      setRouteMode("edit");
      setRouteForm(route);
    } else {
      setRouteMode("add");
      setRouteForm({
        routeId: null,
        name: "",
        description: "",
        pincode: "",
        totalDistance: "",
      });
    }
    setShowRouteModal(true);
  };

  const handleSaveRoute = async () => {
    if (!routeForm.name || !routeForm.pincode || !routeForm.totalDistance) {
      showMessage("Please fill in all required fields", true);
      return;
    }

    setLoading(true);
    try {
      const method = routeMode === "add" ? "POST" : "PUT";
      const endpoint =
        routeMode === "add" ? "/api/routes/create" : "/api/routes/update";

      const response = await fetch(`${API_BASE}${endpoint}`, {
        method: method,
        headers: {
          "Content-Type": "application/json",
          Authorization: `Bearer ${token}`,
        },
        body: JSON.stringify(routeForm),
      });

      const result = await response.json();
      if (result.message || result.routeId) {
        showMessage(
          routeMode === "add"
            ? "Route created successfully"
            : "Route updated successfully",
          false
        );
        setShowRouteModal(false);
        await loadRoutes();
      } else {
        showMessage(result.error || "Failed to save route", true);
      }
    } catch (error) {
      showMessage("Error saving route: " + error.message, true);
    }
    setLoading(false);
  };

  const handleDeleteRoute = async (routeId) => {
    if (!window.confirm("Are you sure you want to delete this route?")) return;

    setLoading(true);
    try {
      const response = await fetch(`${API_BASE}/api/routes/delete`, {
        method: "DELETE",
        headers: {
          "Content-Type": "application/json",
          Authorization: `Bearer ${token}`,
        },
        body: JSON.stringify({ routeId }),
      });

      const result = await response.json();
      if (result.message) {
        showMessage("Route deleted successfully", false);
        await loadRoutes();
      } else {
        showMessage(result.error || "Failed to delete route", true);
      }
    } catch (error) {
      showMessage("Error deleting route: " + error.message, true);
    }
    setLoading(false);
  };

  // ===== EMPLOYEE CRUD OPERATIONS =====
  const openEmployeeModal = (employee = null) => {
    if (employee) {
      setEmployeeMode("edit");
      setEmployeeForm(employee);
    } else {
      setEmployeeMode("add");
      setEmployeeForm({
        empId: null,
        firstName: "",
        lastName: "",
        email: "",
        phone: "",
        role: "",
        department: "",
        managerName: "",
      });
    }
    setShowEmployeeModal(true);
  };

  const handleSaveEmployee = async () => {
    if (
      !employeeForm.firstName ||
      !employeeForm.lastName ||
      !employeeForm.role ||
      !employeeForm.department
    ) {
      showMessage("Please fill in all required fields", true);
      return;
    }

    setLoading(true);
    try {
      const method = employeeMode === "add" ? "POST" : "PUT";
      const endpoint =
        employeeMode === "add"
          ? "/api/employees/create"
          : "/api/employees/update";

      const response = await fetch(`${API_BASE}${endpoint}`, {
        method: method,
        headers: {
          "Content-Type": "application/json",
          Authorization: `Bearer ${token}`,
        },
        body: JSON.stringify(employeeForm),
      });

      const result = await response.json();
      if (result.message || result.employeeId) {
        showMessage(
          employeeMode === "add"
            ? "Employee created successfully"
            : "Employee updated successfully",
          false
        );
        setShowEmployeeModal(false);
        await loadEmployees();
      } else {
        showMessage(result.error || "Failed to save employee", true);
      }
    } catch (error) {
      showMessage("Error saving employee: " + error.message, true);
    }
    setLoading(false);
  };

  const handleDeleteEmployee = async (empId) => {
    if (!window.confirm("Are you sure you want to delete this employee?"))
      return;

    setLoading(true);
    try {
      const response = await fetch(`${API_BASE}/api/employees/delete`, {
        method: "DELETE",
        headers: {
          "Content-Type": "application/json",
          Authorization: `Bearer ${token}`,
        },
        body: JSON.stringify({ empId }),
      });

      const result = await response.json();
      if (result.message) {
        showMessage("Employee deleted successfully", false);
        await loadEmployees();
      } else {
        showMessage(result.error || "Failed to delete employee", true);
      }
    } catch (error) {
      showMessage("Error deleting employee: " + error.message, true);
    }
    setLoading(false);
  };

  // ===== RENDER LOGIN/REGISTER PAGE =====
  if (!isLoggedIn) {
    return (
      <div
        className="d-flex align-items-center justify-content-center"
        style={{ minHeight: "100vh", backgroundColor: "#fff" }}
      >
        <div className="login-container">
          <h1>RouteMaster</h1>
          <p>Logistics Management System</p>

          {error && <Alert variant="danger">{error}</Alert>}
          {successMsg && <Alert variant="success">{successMsg}</Alert>}

          {authMode === "login" ? (
            <div>
              <h4 style={{ marginBottom: "20px", textAlign: "center" }}>
                Login
              </h4>
              <Form.Group className="mb-3">
                <Form.Label>Email</Form.Label>
                <Form.Control
                  type="email"
                  value={email}
                  onChange={(e) => setEmail(e.target.value)}
                  placeholder="Enter your email"
                />
              </Form.Group>

              <Form.Group className="mb-3">
                <Form.Label>Password</Form.Label>
                <Form.Control
                  type="password"
                  value={password}
                  onChange={(e) => setPassword(e.target.value)}
                  placeholder="Enter your password"
                  onKeyPress={(e) => e.key === "Enter" && handleLogin()}
                />
              </Form.Group>

              <Button
                onClick={handleLogin}
                disabled={loading}
                className="w-100"
                style={{
                  backgroundColor: "#000",
                  border: "none",
                  marginBottom: "10px",
                }}
              >
                {loading ? "Logging in..." : "Login"}
              </Button>

              <p
                className="text-center"
                style={{ fontSize: "14px", marginBottom: "10px" }}
              >
                Don't have an account?
              </p>
              <Button
                onClick={() => {
                  setAuthMode("register");
                  setError("");
                  setSuccessMsg("");
                }}
                variant="outline-dark"
                className="w-100"
              >
                Create Account
              </Button>
            </div>
          ) : (
            <div>
              <h4 style={{ marginBottom: "20px", textAlign: "center" }}>
                Register
              </h4>
              <Form.Group className="mb-3">
                <Form.Label>First Name</Form.Label>
                <Form.Control
                  type="text"
                  value={firstName}
                  onChange={(e) => setFirstName(e.target.value)}
                  placeholder="Enter first name"
                />
              </Form.Group>

              <Form.Group className="mb-3">
                <Form.Label>Last Name</Form.Label>
                <Form.Control
                  type="text"
                  value={lastName}
                  onChange={(e) => setLastName(e.target.value)}
                  placeholder="Enter last name"
                />
              </Form.Group>

              <Form.Group className="mb-3">
                <Form.Label>Email</Form.Label>
                <Form.Control
                  type="email"
                  value={email}
                  onChange={(e) => setEmail(e.target.value)}
                  placeholder="Enter your email"
                />
              </Form.Group>

              <Form.Group className="mb-3">
                <Form.Label>Phone</Form.Label>
                <Form.Control
                  type="tel"
                  value={phone}
                  onChange={(e) => setPhone(e.target.value)}
                  placeholder="Enter phone number"
                />
              </Form.Group>

              <Form.Group className="mb-3">
                <Form.Label>Address</Form.Label>
                <Form.Control
                  type="text"
                  value={address}
                  onChange={(e) => setAddress(e.target.value)}
                  placeholder="Enter address"
                />
              </Form.Group>

              <Form.Group className="mb-3">
                <Form.Label>Password</Form.Label>
                <Form.Control
                  type="password"
                  value={password}
                  onChange={(e) => setPassword(e.target.value)}
                  placeholder="Enter password"
                />
              </Form.Group>

              <Button
                onClick={handleRegister}
                disabled={loading}
                className="w-100"
                style={{
                  backgroundColor: "#000",
                  border: "none",
                  marginBottom: "10px",
                }}
              >
                {loading ? "Creating Account..." : "Register"}
              </Button>

              <p
                className="text-center"
                style={{ fontSize: "14px", marginBottom: "10px" }}
              >
                Already have an account?
              </p>
              <Button
                onClick={() => {
                  setAuthMode("login");
                  setError("");
                  setSuccessMsg("");
                }}
                variant="outline-dark"
                className="w-100"
              >
                Login
              </Button>
            </div>
          )}

          <p
            className="text-center mt-3"
            style={{ fontSize: "12px", color: "#666" }}
          >
            Demo: test@example.com / password
          </p>
        </div>
      </div>
    );
  }

  // ===== RENDER MAIN APPLICATION =====
  return (
    <div className="d-flex" style={{ minHeight: "100vh" }}>
      <div
        className={`sidebar ${!sidebarOpen ? "collapsed" : ""}`}
        style={{ width: sidebarOpen ? "250px" : "80px" }}
      >
        <div className="d-flex justify-content-between align-items-center mb-4">
          {sidebarOpen && <h2>RouteMaster</h2>}
          <Button
            variant="dark"
            size="sm"
            onClick={() => setSidebarOpen(!sidebarOpen)}
            style={{
              backgroundColor: "transparent",
              border: "none",
              color: "white",
            }}
          >
            <Menu size={24} />
          </Button>
        </div>

        <div
          className="d-flex flex-column"
          style={{ height: "calc(100vh - 80px)" }}
        >
          <div>
            <NavButton
              icon={<BarChart3 size={20} />}
              label="Dashboard"
              isActive={currentPage === "dashboard"}
              onClick={() => {
                setCurrentPage("dashboard");
                loadDashboard(token);
              }}
              sidebarOpen={sidebarOpen}
            />
            <NavButton
              icon={<Package size={20} />}
              label="Parcels"
              isActive={currentPage === "parcels"}
              onClick={() => {
                setCurrentPage("parcels");
                loadParcels();
              }}
              sidebarOpen={sidebarOpen}
            />
            <NavButton
              icon={<Users size={20} />}
              label="Customers"
              isActive={currentPage === "customers"}
              onClick={() => {
                setCurrentPage("customers");
                loadCustomers();
              }}
              sidebarOpen={sidebarOpen}
            />
            <NavButton
              icon={<Truck size={20} />}
              label="Routes"
              isActive={currentPage === "routes"}
              onClick={() => {
                setCurrentPage("routes");
                loadRoutes();
              }}
              sidebarOpen={sidebarOpen}
            />
            <NavButton
              icon={<Users size={20} />}
              label="Employees"
              isActive={currentPage === "employees"}
              onClick={() => {
                setCurrentPage("employees");
                loadEmployees();
              }}
              sidebarOpen={sidebarOpen}
            />
          </div>

          <Button onClick={handleLogout} className="logout-btn mt-auto">
            <LogOut size={20} />
            {sidebarOpen && <span>Logout</span>}
          </Button>
        </div>
      </div>

      <div style={{ flex: 1 }}>
        <div className="content-header">
          <h1>{currentPage.charAt(0).toUpperCase() + currentPage.slice(1)}</h1>
        </div>

        {error && (
          <div className="m-4">
            <Alert variant="danger">{error}</Alert>
          </div>
        )}
        {successMsg && (
          <div className="m-4">
            <Alert variant="success">{successMsg}</Alert>
          </div>
        )}

        <Container fluid className="p-4">
          {currentPage === "dashboard" && (
            <DashboardPage stats={dashboardStats} />
          )}
          {currentPage === "parcels" && (
            <ParcelsPage
              parcels={parcels}
              onAdd={() => openParcelModal()}
              onEdit={(parcel) => openParcelModal(parcel)}
              onDelete={(parcelId) => handleDeleteParcel(parcelId)}
            />
          )}
          {currentPage === "customers" && (
            <CustomersPage
              customers={customers}
              onAdd={() => openCustomerModal()}
              onEdit={(customer) => openCustomerModal(customer)}
              onDelete={(customerId) => handleDeleteCustomer(customerId)}
            />
          )}
          {currentPage === "routes" && (
            <RoutesPage
              routes={routes}
              onAdd={() => openRouteModal()}
              onEdit={(route) => openRouteModal(route)}
              onDelete={(routeId) => handleDeleteRoute(routeId)}
            />
          )}
          {currentPage === "employees" && (
            <EmployeesPage
              employees={employees}
              onAdd={() => openEmployeeModal()}
              onEdit={(employee) => openEmployeeModal(employee)}
              onDelete={(empId) => handleDeleteEmployee(empId)}
            />
          )}
        </Container>
      </div>

      {/* ===== CUSTOMER MODAL ===== */}
      <Modal
        show={showCustomerModal}
        onHide={() => setShowCustomerModal(false)}
        size="lg"
      >
        <Modal.Header closeButton>
          <Modal.Title>
            {customerMode === "add" ? "Add New Customer" : "Edit Customer"}
          </Modal.Title>
        </Modal.Header>
        <Modal.Body>
          <Form.Group className="mb-3">
            <Form.Label>First Name * (Required)</Form.Label>
            <Form.Control
              type="text"
              value={customerForm.firstName}
              onChange={(e) =>
                setCustomerForm({ ...customerForm, firstName: e.target.value })
              }
              placeholder="Enter customer first name"
            />
          </Form.Group>

          <Form.Group className="mb-3">
            <Form.Label>Last Name * (Required)</Form.Label>
            <Form.Control
              type="text"
              value={customerForm.lastName}
              onChange={(e) =>
                setCustomerForm({ ...customerForm, lastName: e.target.value })
              }
              placeholder="Enter customer last name"
            />
          </Form.Group>

          <Form.Group className="mb-3">
            <Form.Label>Email * (Required)</Form.Label>
            <Form.Control
              type="email"
              value={customerForm.email}
              onChange={(e) =>
                setCustomerForm({ ...customerForm, email: e.target.value })
              }
              placeholder="Enter customer email"
            />
          </Form.Group>

          <Form.Group className="mb-3">
            <Form.Label>Phone * (Required)</Form.Label>
            <Form.Control
              type="tel"
              value={customerForm.phone}
              onChange={(e) =>
                setCustomerForm({ ...customerForm, phone: e.target.value })
              }
              placeholder="Enter phone number"
            />
          </Form.Group>

          <Form.Group className="mb-3">
            <Form.Label>Address * (Required)</Form.Label>
            <Form.Control
              type="text"
              value={customerForm.address}
              onChange={(e) =>
                setCustomerForm({ ...customerForm, address: e.target.value })
              }
              placeholder="Enter customer address"
            />
          </Form.Group>

          {customerMode === "add" && (
            <Form.Group className="mb-3">
              <Form.Label>Password * (Required for new customers)</Form.Label>
              <Form.Control
                type="password"
                value={customerForm.password}
                onChange={(e) =>
                  setCustomerForm({ ...customerForm, password: e.target.value })
                }
                placeholder="Enter password"
              />
            </Form.Group>
          )}
        </Modal.Body>
        <Modal.Footer>
          <Button
            variant="secondary"
            onClick={() => setShowCustomerModal(false)}
          >
            Close
          </Button>
          <Button
            onClick={handleSaveCustomer}
            disabled={loading}
            style={{ backgroundColor: "#000", border: "none" }}
          >
            {loading ? "Saving..." : "Save Customer"}
          </Button>
        </Modal.Footer>
      </Modal>

      {/* ===== PARCEL MODAL ===== */}
      <Modal
        show={showParcelModal}
        onHide={() => setShowParcelModal(false)}
        size="lg"
      >
        <Modal.Header closeButton>
          <Modal.Title>
            {parcelMode === "add" ? "Add New Parcel" : "Edit Parcel"}
          </Modal.Title>
        </Modal.Header>
        <Modal.Body>
          <Form.Group className="mb-3">
            <Form.Label>Sender Name * (Required)</Form.Label>
            <Form.Control
              type="text"
              value={parcelForm.senderName}
              onChange={(e) =>
                setParcelForm({ ...parcelForm, senderName: e.target.value })
              }
              placeholder="Enter sender name"
            />
          </Form.Group>

          <Form.Group className="mb-3">
            <Form.Label>Receiver Name * (Required)</Form.Label>
            <Form.Control
              type="text"
              value={parcelForm.receiverName}
              onChange={(e) =>
                setParcelForm({ ...parcelForm, receiverName: e.target.value })
              }
              placeholder="Enter receiver name"
            />
          </Form.Group>

          <Form.Group className="mb-3">
            <Form.Label>Destination Address * (Required)</Form.Label>
            <Form.Control
              type="text"
              value={parcelForm.destinationAddress}
              onChange={(e) =>
                setParcelForm({
                  ...parcelForm,
                  destinationAddress: e.target.value,
                })
              }
              placeholder="Enter destination address"
            />
          </Form.Group>

          <Form.Group className="mb-3">
            <Form.Label>Destination Pincode * (Required)</Form.Label>
            <Form.Control
              type="text"
              value={parcelForm.destinationPincode}
              onChange={(e) =>
                setParcelForm({
                  ...parcelForm,
                  destinationPincode: e.target.value,
                })
              }
              placeholder="Enter destination pincode"
            />
          </Form.Group>

          <Form.Group className="mb-3">
            <Form.Label>Parcel Status *</Form.Label>
            <Form.Select
              value={parcelForm.parcelStatus}
              onChange={(e) =>
                setParcelForm({ ...parcelForm, parcelStatus: e.target.value })
              }
            >
              <option value="PENDING">PENDING</option>
              <option value="IN_TRANSIT">IN_TRANSIT</option>
              <option value="DELIVERED">DELIVERED</option>
              <option value="DELAYED">DELAYED</option>
            </Form.Select>
          </Form.Group>

          <Form.Group className="mb-3">
            <Form.Label>Created Date *</Form.Label>
            <Form.Control
              type="date"
              value={parcelForm.createdDate}
              onChange={(e) =>
                setParcelForm({ ...parcelForm, createdDate: e.target.value })
              }
            />
          </Form.Group>

          <Form.Group className="mb-3">
            <Form.Label>Customer ID * (Required)</Form.Label>
            <Form.Control
              type="number"
              value={parcelForm.customerId}
              onChange={(e) =>
                setParcelForm({ ...parcelForm, customerId: e.target.value })
              }
              placeholder="Enter customer ID"
            />
          </Form.Group>

          <Form.Group className="mb-3">
            <Form.Label>Route ID * (Required)</Form.Label>
            <Form.Control
              type="number"
              value={parcelForm.routeId}
              onChange={(e) =>
                setParcelForm({ ...parcelForm, routeId: e.target.value })
              }
              placeholder="Enter route ID"
            />
          </Form.Group>

          <Form.Group className="mb-3">
            <Form.Label>Total Payment * (Required)</Form.Label>
            <Form.Control
              type="number"
              value={parcelForm.totalPayment}
              onChange={(e) =>
                setParcelForm({ ...parcelForm, totalPayment: e.target.value })
              }
              placeholder="Enter total payment"
            />
          </Form.Group>
        </Modal.Body>
        <Modal.Footer>
          <Button variant="secondary" onClick={() => setShowParcelModal(false)}>
            Close
          </Button>
          <Button
            onClick={handleSaveParcel}
            disabled={loading}
            style={{ backgroundColor: "#000", border: "none" }}
          >
            {loading ? "Saving..." : "Save Parcel"}
          </Button>
        </Modal.Footer>
      </Modal>

      {/* ===== ROUTE MODAL ===== */}
      <Modal
        show={showRouteModal}
        onHide={() => setShowRouteModal(false)}
        size="lg"
      >
        <Modal.Header closeButton>
          <Modal.Title>
            {routeMode === "add" ? "Add New Route" : "Edit Route"}
          </Modal.Title>
        </Modal.Header>
        <Modal.Body>
          <Form.Group className="mb-3">
            <Form.Label>Route Name * (Required)</Form.Label>
            <Form.Control
              type="text"
              value={routeForm.name}
              onChange={(e) =>
                setRouteForm({ ...routeForm, name: e.target.value })
              }
              placeholder="e.g., Route-Mumbai-01"
            />
          </Form.Group>

          <Form.Group className="mb-3">
            <Form.Label>Description (Optional)</Form.Label>
            <Form.Control
              type="text"
              value={routeForm.description}
              onChange={(e) =>
                setRouteForm({ ...routeForm, description: e.target.value })
              }
              placeholder="Enter route description"
            />
          </Form.Group>

          <Form.Group className="mb-3">
            <Form.Label>Pincode * (Required)</Form.Label>
            <Form.Control
              type="text"
              value={routeForm.pincode}
              onChange={(e) =>
                setRouteForm({ ...routeForm, pincode: e.target.value })
              }
              placeholder="Enter pincode"
            />
          </Form.Group>

          <Form.Group className="mb-3">
            <Form.Label>Total Distance (km) * (Required)</Form.Label>
            <Form.Control
              type="number"
              value={routeForm.totalDistance}
              onChange={(e) =>
                setRouteForm({ ...routeForm, totalDistance: e.target.value })
              }
              placeholder="Enter total distance"
            />
          </Form.Group>
        </Modal.Body>
        <Modal.Footer>
          <Button variant="secondary" onClick={() => setShowRouteModal(false)}>
            Close
          </Button>
          <Button
            onClick={handleSaveRoute}
            disabled={loading}
            style={{ backgroundColor: "#000", border: "none" }}
          >
            {loading ? "Saving..." : "Save Route"}
          </Button>
        </Modal.Footer>
      </Modal>

      {/* ===== EMPLOYEE MODAL ===== */}
      <Modal
        show={showEmployeeModal}
        onHide={() => setShowEmployeeModal(false)}
        size="lg"
      >
        <Modal.Header closeButton>
          <Modal.Title>
            {employeeMode === "add" ? "Add New Employee" : "Edit Employee"}
          </Modal.Title>
        </Modal.Header>
        <Modal.Body>
          <Form.Group className="mb-3">
            <Form.Label>First Name * (Required)</Form.Label>
            <Form.Control
              type="text"
              value={employeeForm.firstName}
              onChange={(e) =>
                setEmployeeForm({ ...employeeForm, firstName: e.target.value })
              }
              placeholder="Enter first name"
            />
          </Form.Group>

          <Form.Group className="mb-3">
            <Form.Label>Last Name * (Required)</Form.Label>
            <Form.Control
              type="text"
              value={employeeForm.lastName}
              onChange={(e) =>
                setEmployeeForm({ ...employeeForm, lastName: e.target.value })
              }
              placeholder="Enter last name"
            />
          </Form.Group>

          <Form.Group className="mb-3">
            <Form.Label>Email (Optional)</Form.Label>
            <Form.Control
              type="email"
              value={employeeForm.email}
              onChange={(e) =>
                setEmployeeForm({ ...employeeForm, email: e.target.value })
              }
              placeholder="Enter email"
            />
          </Form.Group>

          <Form.Group className="mb-3">
            <Form.Label>Phone (Optional)</Form.Label>
            <Form.Control
              type="tel"
              value={employeeForm.phone}
              onChange={(e) =>
                setEmployeeForm({ ...employeeForm, phone: e.target.value })
              }
              placeholder="Enter phone number"
            />
          </Form.Group>

          <Form.Group className="mb-3">
            <Form.Label>Role * (Required)</Form.Label>
            <Form.Control
              type="text"
              value={employeeForm.role}
              onChange={(e) =>
                setEmployeeForm({ ...employeeForm, role: e.target.value })
              }
              placeholder="e.g., DRIVER, MANAGER"
            />
          </Form.Group>

          <Form.Group className="mb-3">
            <Form.Label>Department * (Required)</Form.Label>
            <Form.Control
              type="text"
              value={employeeForm.department}
              onChange={(e) =>
                setEmployeeForm({ ...employeeForm, department: e.target.value })
              }
              placeholder="e.g., Delivery, Operations"
            />
          </Form.Group>

          <Form.Group className="mb-3">
            <Form.Label>Manager Name (Optional)</Form.Label>
            <Form.Control
              type="text"
              value={employeeForm.managerName}
              onChange={(e) =>
                setEmployeeForm({
                  ...employeeForm,
                  managerName: e.target.value,
                })
              }
              placeholder="Enter manager name"
            />
          </Form.Group>
        </Modal.Body>
        <Modal.Footer>
          <Button
            variant="secondary"
            onClick={() => setShowEmployeeModal(false)}
          >
            Close
          </Button>
          <Button
            onClick={handleSaveEmployee}
            disabled={loading}
            style={{ backgroundColor: "#000", border: "none" }}
          >
            {loading ? "Saving..." : "Save Employee"}
          </Button>
        </Modal.Footer>
      </Modal>
    </div>
  );
}

const NavButton = ({ icon, label, isActive, onClick, sidebarOpen }) => (
  <button
    onClick={onClick}
    className={`nav-button ${isActive ? "active" : ""}`}
  >
    {icon}
    {sidebarOpen && <span>{label}</span>}
  </button>
);

const DashboardPage = ({ stats }) => (
  <div>
    <Row className="mb-4">
      <Col md={3} sm={6} className="mb-3">
        <StatCard title="Total Customers" value={stats.totalCustomers} />
      </Col>
      <Col md={3} sm={6} className="mb-3">
        <StatCard title="Total Parcels" value={stats.totalParcels} />
      </Col>
      <Col md={3} sm={6} className="mb-3">
        <StatCard
          title="Total Payment"
          value={`₹${stats.totalPayment.toFixed(2)}`}
        />
      </Col>
      <Col md={3} sm={6} className="mb-3">
        <StatCard
          title="Active Routes"
          value={Object.keys(stats.parcelsByRoute).length}
        />
      </Col>
    </Row>

    <Card className="stat-card">
      <Card.Body>
        <Card.Title>Parcels by Route</Card.Title>
        {Object.entries(stats.parcelsByRoute).length > 0 ? (
          <div>
            {Object.entries(stats.parcelsByRoute).map(([route, count]) => (
              <div
                key={route}
                className="d-flex justify-content-between p-2 border-bottom"
              >
                <span style={{ fontWeight: "bold" }}>{route}</span>
                <span>{count} parcels</span>
              </div>
            ))}
          </div>
        ) : (
          <p style={{ color: "#666" }}>No route data available</p>
        )}
      </Card.Body>
    </Card>
  </div>
);

const StatCard = ({ title, value }) => (
  <div className="stat-card">
    <h6>{title}</h6>
    <div className="value">{value}</div>
  </div>
);

const ParcelsPage = ({ parcels, onAdd, onEdit, onDelete }) => (
  <div>
    <div className="mb-3">
      <Button
        onClick={onAdd}
        style={{ backgroundColor: "#000", border: "none" }}
      >
        <Plus size={20} style={{ marginRight: "5px" }} /> Add Parcel
      </Button>
    </div>
    <div className="table-container">
      <Table striped hover>
        <thead>
          <tr>
            <th>Tracking #</th>
            <th>Sender</th>
            <th>Receiver</th>
            <th>Destination</th>
            <th>Status</th>
            <th>Payment</th>
            <th>Actions</th>
          </tr>
        </thead>
        <tbody>
          {parcels.map((parcel, idx) => (
            <tr key={idx}>
              <td style={{ fontWeight: "bold" }}>{parcel.trackingNumber}</td>
              <td>{parcel.senderName}</td>
              <td>{parcel.receiverName}</td>
              <td>{parcel.destinationAddress}</td>
              <td>
                <span
                  className={`status-badge ${getStatusClass(
                    parcel.parcelStatus
                  )}`}
                >
                  {parcel.parcelStatus}
                </span>
              </td>
              <td style={{ fontWeight: "bold" }}>₹{parcel.totalPayment}</td>
              <td>
                <Button
                  size="sm"
                  variant="outline-dark"
                  onClick={() => onEdit(parcel)}
                  style={{ marginRight: "5px" }}
                >
                  <Edit size={16} />
                </Button>
                <Button
                  size="sm"
                  variant="outline-danger"
                  onClick={() => onDelete(parcel.parcelId)}
                >
                  <Trash2 size={16} />
                </Button>
              </td>
            </tr>
          ))}
        </tbody>
      </Table>
      {parcels.length === 0 && (
        <div className="p-4 text-center" style={{ color: "#666" }}>
          No parcels found
        </div>
      )}
    </div>
  </div>
);

const CustomersPage = ({ customers, onAdd, onEdit, onDelete }) => (
  <div>
    <div className="mb-3">
      <Button
        onClick={onAdd}
        style={{ backgroundColor: "#000", border: "none" }}
      >
        <Plus size={20} style={{ marginRight: "5px" }} /> Add Customer
      </Button>
    </div>
    <div className="table-container">
      <Table striped hover>
        <thead>
          <tr>
            <th>Name</th>
            <th>Email</th>
            <th>Phone</th>
            <th>Address</th>
            <th>Actions</th>
          </tr>
        </thead>
        <tbody>
          {customers.map((customer) => (
            <tr key={customer.customerId}>
              <td style={{ fontWeight: "bold" }}>
                {customer.firstName} {customer.lastName}
              </td>
              <td>{customer.email}</td>
              <td>{customer.phone}</td>
              <td>{customer.address}</td>
              <td>
                <Button
                  size="sm"
                  variant="outline-dark"
                  onClick={() => onEdit(customer)}
                  style={{ marginRight: "5px" }}
                >
                  <Edit size={16} />
                </Button>
                <Button
                  size="sm"
                  variant="outline-danger"
                  onClick={() => onDelete(customer.customerId)}
                >
                  <Trash2 size={16} />
                </Button>
              </td>
            </tr>
          ))}
        </tbody>
      </Table>
      {customers.length === 0 && (
        <div className="p-4 text-center" style={{ color: "#666" }}>
          No customers found
        </div>
      )}
    </div>
  </div>
);

const RoutesPage = ({ routes, onAdd, onEdit, onDelete }) => (
  <div>
    <div className="mb-3">
      <Button
        onClick={onAdd}
        style={{ backgroundColor: "#000", border: "none" }}
      >
        <Plus size={20} style={{ marginRight: "5px" }} /> Add Route
      </Button>
    </div>
    <div className="table-container">
      <Table striped hover>
        <thead>
          <tr>
            <th>Route Name</th>
            <th>Description</th>
            <th>Pincode</th>
            <th>Distance (km)</th>
            <th>Actions</th>
          </tr>
        </thead>
        <tbody>
          {routes.map((route) => (
            <tr key={route.routeId}>
              <td style={{ fontWeight: "bold" }}>{route.name}</td>
              <td>{route.description}</td>
              <td>{route.pincode}</td>
              <td>{route.totalDistance}</td>
              <td>
                <Button
                  size="sm"
                  variant="outline-dark"
                  onClick={() => onEdit(route)}
                  style={{ marginRight: "5px" }}
                >
                  <Edit size={16} />
                </Button>
                <Button
                  size="sm"
                  variant="outline-danger"
                  onClick={() => onDelete(route.routeId)}
                >
                  <Trash2 size={16} />
                </Button>
              </td>
            </tr>
          ))}
        </tbody>
      </Table>
      {routes.length === 0 && (
        <div className="p-4 text-center" style={{ color: "#666" }}>
          No routes found
        </div>
      )}
    </div>
  </div>
);

const EmployeesPage = ({ employees, onAdd, onEdit, onDelete }) => (
  <div>
    <div className="mb-3">
      <Button
        onClick={onAdd}
        style={{ backgroundColor: "#000", border: "none" }}
      >
        <Plus size={20} style={{ marginRight: "5px" }} /> Add Employee
      </Button>
    </div>
    <div className="table-container">
      <Table striped hover>
        <thead>
          <tr>
            <th>Name</th>
            <th>Email</th>
            <th>Phone</th>
            <th>Role</th>
            <th>Department</th>
            <th>Manager</th>
            <th>Actions</th>
          </tr>
        </thead>
        <tbody>
          {employees.map((employee) => (
            <tr key={employee.employeeId}>
              <td style={{ fontWeight: "bold" }}>
                {employee.firstName} {employee.lastName}
              </td>
              <td>{employee.email}</td>
              <td>{employee.phone}</td>
              <td>{employee.role}</td>
              <td>{employee.department}</td>
              <td>{employee.managerName}</td>
              <td>
                <Button
                  size="sm"
                  variant="outline-dark"
                  onClick={() => onEdit(employee)}
                  style={{ marginRight: "5px" }}
                >
                  <Edit size={16} />
                </Button>
                <Button
                  size="sm"
                  variant="outline-danger"
                  onClick={() => onDelete(employee.employeeId)}
                >
                  <Trash2 size={16} />
                </Button>
              </td>
            </tr>
          ))}
        </tbody>
      </Table>
      {employees.length === 0 && (
        <div className="p-4 text-center" style={{ color: "#666" }}>
          No employees found
        </div>
      )}
    </div>
  </div>
);

const getStatusClass = (status) => {
  switch (status) {
    case "PENDING":
      return "pending";
    case "IN_TRANSIT":
      return "in-transit";
    case "DELIVERED":
      return "delivered";
    case "DELAYED":
      return "delayed";
    default:
      return "pending";
  }
};

export default RouteMasterFrontend;
