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

      const 