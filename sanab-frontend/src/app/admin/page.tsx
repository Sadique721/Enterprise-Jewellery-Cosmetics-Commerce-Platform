'use client';

import React, { useState, useEffect } from 'react';
import {
  DollarSign, ShoppingBag, Users, Package, TrendingUp, AlertTriangle,
  Download, ArrowUpRight, Plus, Edit3, Trash2, CheckCircle2, Shield,
  FileSpreadsheet, FileText, Send, RefreshCw, Filter, Search
} from 'lucide-react';
import Link from 'next/link';

export default function AdminDashboardPage() {
  const [activeTab, setActiveTab] = useState<'analytics' | 'products' | 'orders' | 'users' | 'exports'>('analytics');
  const [showAddProductModal, setShowAddProductModal] = useState(false);

  // Sample Products State for Admin CRUD
  const [productsList, setProductsList] = useState([
    {
      id: '1',
      title: 'Royal Heritage Kundan Choker Necklace',
      sku: 'JWL-KND-001',
      category: 'Bridal Jewellery',
      price: 185000,
      stock: 2,
      purity: '22K Hallmarked Gold',
      status: 'ACTIVE',
    },
    {
      id: '2',
      title: 'Solitaire Diamond Engagement Ring',
      sku: 'JWL-DMD-002',
      category: 'Solitaire Rings',
      price: 145000,
      stock: 1,
      purity: 'VVS1 IGI Certified',
      status: 'ACTIVE',
    },
    {
      id: '3',
      title: '24K Gold Infused Regenerative Serum',
      sku: 'CSM-SRM-001',
      category: 'Luxury Skincare',
      price: 8500,
      stock: 4,
      purity: 'Clinical Grade',
      status: 'ACTIVE',
    },
    {
      id: '4',
      title: 'Velvet Matte Royal Ruby Lipstick',
      sku: 'CSM-LPS-002',
      category: 'Cosmetics',
      price: 3200,
      stock: 18,
      purity: 'Organic Pigment',
      status: 'ACTIVE',
    },
  ]);

  // Form State for Add Product
  const [newTitle, setNewTitle] = useState('');
  const [newSku, setNewSku] = useState('');
  const [newCategory, setNewCategory] = useState('Bridal Jewellery');
  const [newPrice, setNewPrice] = useState('');
  const [newStock, setNewStock] = useState('');
  const [newPurity, setNewPurity] = useState('22K Gold');

  // Sample Orders State for Order Management & Status Updates
  const [ordersList, setOrdersList] = useState([
    {
      id: 'SNB-ORD-9021',
      customer: 'Sadique Amin (Customer)',
      email: 'mdsadiqueamin721721@gmail.com',
      amount: '₹1,85,000',
      status: 'PAID',
      date: '27 July 2026',
    },
    {
      id: 'SNB-ORD-9022',
      customer: 'Priya Sharma',
      email: 'priya.sharma@example.com',
      amount: '₹1,45,000',
      status: 'PROCESSING',
      date: '27 July 2026',
    },
    {
      id: 'SNB-ORD-9023',
      customer: 'Ananya Roy',
      email: 'ananya.roy@example.com',
      amount: '₹8,500',
      status: 'SHIPPED',
      date: '26 July 2026',
    },
    {
      id: 'SNB-ORD-9024',
      customer: 'Vikram Mehta',
      email: 'vikram.m@example.com',
      amount: '₹1,28,000',
      status: 'DELIVERED',
      date: '25 July 2026',
    },
  ]);

  // User Accounts State
  const [usersList, setUsersList] = useState([
    {
      id: '00000000-0000-0000-0000-000000000786',
      name: 'Sadique Amin (Admin)',
      email: 'mdsadiqueamin721786@gmail.com',
      role: 'SUPER_ADMIN',
      status: 'ACTIVE',
    },
    {
      id: '00000000-0000-0000-0000-000000000721',
      name: 'Sadique Amin (Customer)',
      email: 'mdsadiqueamin721721@gmail.com',
      role: 'CUSTOMER',
      status: 'ACTIVE',
    },
  ]);

  useEffect(() => {
    try {
      const stored = localStorage.getItem('sanab_current_user');
      if (stored) {
        const parsed = JSON.parse(stored);
        if (parsed.email && !usersList.some((u) => u.email === parsed.email)) {
          setUsersList((prev) => [
            ...prev,
            {
              id: `user-${Date.now()}`,
              name: parsed.name || 'Registered Customer',
              email: parsed.email,
              role: 'CUSTOMER',
              status: 'ACTIVE',
            },
          ]);
        }
      }
    } catch (err) {
      console.warn('Failed to parse user session for admin list:', err);
    }
  }, []);

  // Handlers
  const handleAddProduct = (e: React.FormEvent) => {
    e.preventDefault();
    if (!newTitle || !newPrice) return;
    const newItem = {
      id: String(Date.now()),
      title: newTitle,
      sku: newSku || `JWL-${Math.floor(Math.random() * 900 + 100)}`,
      category: newCategory,
      price: Number(newPrice),
      stock: Number(newStock) || 10,
      purity: newPurity,
      status: 'ACTIVE',
    };
    setProductsList([newItem, ...productsList]);
    setNewTitle('');
    setNewSku('');
    setNewPrice('');
    setNewStock('');
    setShowAddProductModal(false);
  };

  const handleUpdateOrderStatus = (orderId: string, newStatus: string) => {
    setOrdersList(
      ordersList.map((o) => (o.id === orderId ? { ...o, status: newStatus } : o))
    );
  };

  const handleToggleUserStatus = (userId: string) => {
    setUsersList(
      usersList.map((u) =>
        u.id === userId ? { ...u, status: u.status === 'ACTIVE' ? 'SUSPENDED' : 'ACTIVE' } : u
      )
    );
  };

  const handleExport = (type: 'excel' | 'word', entity: string) => {
    const url = `http://localhost:8080/api/admin/export/${entity}/${type}`;
    window.open(url, '_blank');
  };

  return (
    <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-12 space-y-10">
      {/* Executive Header */}
      <div className="flex flex-col md:flex-row md:items-center justify-between gap-4 border-b border-white/10 pb-6">
        <div>
          <span className="text-xs uppercase tracking-widest text-gold-400 font-bold">SANAB EXECUTIVE CONSOLE</span>
          <h1 className="text-3xl font-bold text-white font-serif mt-1">Platform Admin Management Suite</h1>
          <p className="text-xs text-gray-400 font-light mt-1">
            Manage Products, Select & Update Orders, Control Customer Accounts, and Dispatch BI Reports.
          </p>
        </div>

        <div className="flex items-center gap-3">
          <button
            onClick={() => setShowAddProductModal(true)}
            className="flex items-center gap-1.5 px-4 py-2 rounded-lg bg-gold-500 text-black text-xs font-semibold hover:bg-gold-400 transition-colors shadow-lg shadow-gold-500/10"
          >
            <Plus className="w-4 h-4" /> Add New Product
          </button>
          <Link
            href="/account"
            className="flex items-center gap-1.5 px-4 py-2 rounded-lg bg-white/5 text-gray-300 border border-white/10 text-xs font-semibold hover:bg-white/10 transition-colors"
          >
            <Shield className="w-4 h-4 text-gold-400" /> Switch to VIP Account
          </Link>
        </div>
      </div>

      {/* Admin Navigation Tabs */}
      <div className="flex items-center gap-2 overflow-x-auto border-b border-white/10 pb-3">
        {[
          { id: 'analytics', label: 'Dashboard KPIs', icon: TrendingUp },
          { id: 'products', label: 'Product Inventory & Add Product', icon: Package },
          { id: 'orders', label: 'Order Selection & Management', icon: ShoppingBag },
          { id: 'users', label: 'User & Staff Accounts', icon: Users },
          { id: 'exports', label: 'Excel/Word BI Reports', icon: FileSpreadsheet },
        ].map((tab) => {
          const Icon = tab.icon;
          const isActive = activeTab === tab.id;
          return (
            <button
              key={tab.id}
              onClick={() => setActiveTab(tab.id as any)}
              className={`flex items-center gap-2 px-4 py-2.5 rounded-lg text-xs font-semibold transition-all whitespace-nowrap ${
                isActive
                  ? 'bg-gold-500 text-black shadow-md shadow-gold-500/10'
                  : 'bg-white/5 text-gray-300 hover:bg-white/10 hover:text-white'
              }`}
            >
              <Icon className="w-4 h-4" />
              {tab.label}
            </button>
          );
        })}
      </div>

      {/* TAB 1: DASHBOARD KPIS */}
      {activeTab === 'analytics' && (
        <div className="space-y-10">
          <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-6">
            <div className="glass-card p-6 rounded-xl border border-white/10 space-y-3">
              <div className="flex items-center justify-between text-gold-400">
                <span className="text-xs font-semibold uppercase tracking-wider text-gray-400">Total Revenue</span>
                <DollarSign className="w-5 h-5" />
              </div>
              <div className="text-3xl font-serif font-bold text-white">₹12,50,000</div>
              <div className="flex items-center gap-1 text-xs text-emerald-400 font-medium">
                <ArrowUpRight className="w-4 h-4" /> +24.8% vs last month
              </div>
            </div>

            <div className="glass-card p-6 rounded-xl border border-white/10 space-y-3">
              <div className="flex items-center justify-between text-gold-400">
                <span className="text-xs font-semibold uppercase tracking-wider text-gray-400">Total Orders</span>
                <ShoppingBag className="w-5 h-5" />
              </div>
              <div className="text-3xl font-serif font-bold text-white">1,420</div>
              <div className="flex items-center gap-1 text-xs text-emerald-400 font-medium">
                <ArrowUpRight className="w-4 h-4" /> +18.2% conversion rate
              </div>
            </div>

            <div className="glass-card p-6 rounded-xl border border-white/10 space-y-3">
              <div className="flex items-center justify-between text-gold-400">
                <span className="text-xs font-semibold uppercase tracking-wider text-gray-400">Active Customers</span>
                <Users className="w-5 h-5" />
              </div>
              <div className="text-3xl font-serif font-bold text-white">890</div>
              <div className="flex items-center gap-1 text-xs text-emerald-400 font-medium">
                <ArrowUpRight className="w-4 h-4" /> 88.4% retention rate
              </div>
            </div>

            <div className="glass-card p-6 rounded-xl border border-white/10 space-y-3">
              <div className="flex items-center justify-between text-gold-400">
                <span className="text-xs font-semibold uppercase tracking-wider text-gray-400">Avg Order Value</span>
                <TrendingUp className="w-5 h-5" />
              </div>
              <div className="text-3xl font-serif font-bold text-white">₹880</div>
              <div className="flex items-center gap-1 text-xs text-emerald-400 font-medium">
                <ArrowUpRight className="w-4 h-4" /> High-margin basket
              </div>
            </div>
          </div>
        </div>
      )}

      {/* TAB 2: PRODUCT MANAGEMENT & ADD PRODUCT */}
      {activeTab === 'products' && (
        <div className="glass-card p-6 rounded-xl space-y-6">
          <div className="flex items-center justify-between border-b border-white/10 pb-4">
            <div>
              <h2 className="text-lg font-bold text-white font-serif">Product Catalog Management</h2>
              <p className="text-xs text-gray-400 mt-0.5">Add, view, and manage jewellery & cosmetics inventory.</p>
            </div>
            <button
              onClick={() => setShowAddProductModal(true)}
              className="flex items-center gap-1.5 px-3.5 py-2 rounded-lg bg-gold-500 text-black text-xs font-semibold hover:bg-gold-400 transition-colors"
            >
              <Plus className="w-4 h-4" /> Add Product
            </button>
          </div>

          <div className="overflow-x-auto">
            <table className="w-full text-left text-xs text-gray-300">
              <thead className="text-gray-400 border-b border-white/10 font-semibold uppercase tracking-wider">
                <tr>
                  <th className="py-3 px-3">SKU</th>
                  <th className="py-3 px-3">Product Name</th>
                  <th className="py-3 px-3">Category</th>
                  <th className="py-3 px-3">Price</th>
                  <th className="py-3 px-3">Stock Qty</th>
                  <th className="py-3 px-3">Purity / Grade</th>
                  <th className="py-3 px-3 text-right">Actions</th>
                </tr>
              </thead>
              <tbody className="divide-y divide-white/5">
                {productsList.map((p) => (
                  <tr key={p.id} className="hover:bg-white/5 transition-colors">
                    <td className="py-3 px-3 font-mono text-gold-300 font-medium">{p.sku}</td>
                    <td className="py-3 px-3 font-semibold text-white">{p.title}</td>
                    <td className="py-3 px-3 text-gray-400">{p.category}</td>
                    <td className="py-3 px-3 font-bold text-gold-400">₹{p.price.toLocaleString()}</td>
                    <td className="py-3 px-3">
                      <span className={`px-2 py-0.5 rounded text-[10px] font-bold ${p.stock <= 2 ? 'bg-red-500/20 text-red-400' : 'bg-emerald-500/20 text-emerald-400'}`}>
                        {p.stock} units
                      </span>
                    </td>
                    <td className="py-3 px-3 text-gray-300">{p.purity}</td>
                    <td className="py-3 px-3 text-right space-x-2">
                      <button className="p-1.5 rounded bg-white/5 hover:bg-white/10 text-gray-300">
                        <Edit3 className="w-3.5 h-3.5" />
                      </button>
                      <button className="p-1.5 rounded bg-red-500/10 hover:bg-red-500/20 text-red-400">
                        <Trash2 className="w-3.5 h-3.5" />
                      </button>
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        </div>
      )}

      {/* TAB 3: ORDER SELECTION & MANAGEMENT */}
      {activeTab === 'orders' && (
        <div className="glass-card p-6 rounded-xl space-y-6">
          <div className="flex items-center justify-between border-b border-white/10 pb-4">
            <div>
              <h2 className="text-lg font-bold text-white font-serif">Order Selection & Status State Machine</h2>
              <p className="text-xs text-gray-400 mt-0.5">Select orders to view details, dispatch shipments, or update order statuses.</p>
            </div>
            <span className="text-xs text-gold-400 font-semibold">{ordersList.length} Active Orders</span>
          </div>

          <div className="overflow-x-auto">
            <table className="w-full text-left text-xs text-gray-300">
              <thead className="text-gray-400 border-b border-white/10 font-semibold uppercase tracking-wider">
                <tr>
                  <th className="py-3 px-3">Order Number</th>
                  <th className="py-3 px-3">Customer Name</th>
                  <th className="py-3 px-3">Email Address</th>
                  <th className="py-3 px-3">Total Amount</th>
                  <th className="py-3 px-3">Order Date</th>
                  <th className="py-3 px-3">Select & Change Status</th>
                </tr>
              </thead>
              <tbody className="divide-y divide-white/5">
                {ordersList.map((o) => (
                  <tr key={o.id} className="hover:bg-white/5 transition-colors">
                    <td className="py-3 px-3 font-mono text-gold-300 font-bold">{o.id}</td>
                    <td className="py-3 px-3 font-medium text-white">{o.customer}</td>
                    <td className="py-3 px-3 text-gray-400">{o.email}</td>
                    <td className="py-3 px-3 font-semibold text-white">{o.amount}</td>
                    <td className="py-3 px-3 text-gray-400">{o.date}</td>
                    <td className="py-3 px-3">
                      <select
                        value={o.status}
                        onChange={(e) => handleUpdateOrderStatus(o.id, e.target.value)}
                        className="bg-black/60 border border-gold-500/40 rounded px-2 py-1 text-xs text-gold-300 font-semibold focus:outline-none focus:border-gold-400"
                      >
                        <option value="PENDING_PAYMENT">PENDING_PAYMENT</option>
                        <option value="PAID">PAID</option>
                        <option value="PROCESSING">PROCESSING</option>
                        <option value="SHIPPED">SHIPPED</option>
                        <option value="DELIVERED">DELIVERED</option>
                        <option value="CANCELLED">CANCELLED</option>
                      </select>
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        </div>
      )}

      {/* TAB 4: USER & STAFF ACCOUNTS */}
      {activeTab === 'users' && (
        <div className="glass-card p-6 rounded-xl space-y-6">
          <div className="flex items-center justify-between border-b border-white/10 pb-4">
            <div>
              <h2 className="text-lg font-bold text-white font-serif">Platform Accounts & Security Controls</h2>
              <p className="text-xs text-gray-400 mt-0.5">Manage customer profiles, admin permissions, and active statuses.</p>
            </div>
          </div>

          <div className="overflow-x-auto">
            <table className="w-full text-left text-xs text-gray-300">
              <thead className="text-gray-400 border-b border-white/10 font-semibold uppercase tracking-wider">
                <tr>
                  <th className="py-3 px-3">User ID</th>
                  <th className="py-3 px-3">User Name</th>
                  <th className="py-3 px-3">Email Address</th>
                  <th className="py-3 px-3">Assigned Role</th>
                  <th className="py-3 px-3">Account Status</th>
                  <th className="py-3 px-3 text-right">Action</th>
                </tr>
              </thead>
              <tbody className="divide-y divide-white/5">
                {usersList.map((u) => (
                  <tr key={u.id} className="hover:bg-white/5 transition-colors">
                    <td className="py-3 px-3 font-mono text-gray-400">{u.id.substring(0, 18)}...</td>
                    <td className="py-3 px-3 font-semibold text-white">{u.name}</td>
                    <td className="py-3 px-3 text-gold-300 font-mono">{u.email}</td>
                    <td className="py-3 px-3 font-bold text-gold-400">{u.role}</td>
                    <td className="py-3 px-3">
                      <span className={`px-2 py-0.5 rounded text-[10px] font-bold ${u.status === 'ACTIVE' ? 'bg-emerald-500/20 text-emerald-400' : 'bg-red-500/20 text-red-400'}`}>
                        {u.status}
                      </span>
                    </td>
                    <td className="py-3 px-3 text-right">
                      <button
                        onClick={() => handleToggleUserStatus(u.id)}
                        className={`px-3 py-1 rounded text-[11px] font-semibold transition-colors ${
                          u.status === 'ACTIVE'
                            ? 'bg-red-500/10 text-red-400 hover:bg-red-500/20 border border-red-500/30'
                            : 'bg-emerald-500/10 text-emerald-400 hover:bg-emerald-500/20 border border-emerald-500/30'
                        }`}
                      >
                        {u.status === 'ACTIVE' ? 'Suspend Account' : 'Activate Account'}
                      </button>
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        </div>
      )}

      {/* TAB 5: EXCEL/WORD BI REPORTS */}
      {activeTab === 'exports' && (
        <div className="glass-card p-6 rounded-xl space-y-6">
          <div className="border-b border-white/10 pb-4">
            <h2 className="text-lg font-bold text-white font-serif">Executive BI Reports & Data Exports</h2>
            <p className="text-xs text-gray-400 mt-0.5">Download styled Excel (.xlsx) and Word (.doc) exports generated by Apache POI.</p>
          </div>

          <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6 text-xs">
            {[
              { entity: 'orders', title: 'Customer Orders Data', desc: 'Order ID, Customer ID, Grand Total, Statuses, Dates' },
              { entity: 'products', title: 'Product Inventory Data', desc: 'SKU, Title, Base Price, Category ID, Brand ID' },
              { entity: 'users', title: 'User Account Directory', desc: 'User ID, Email, First Name, Last Name, Phone, Role' },
              { entity: 'payments', title: 'Payment Transactions Log', desc: 'Transaction ID, Order ID, Amount, Gateway Method' },
              { entity: 'reviews', title: 'Product Rating Reviews', desc: 'Review ID, Product ID, Customer ID, Rating Stars, Comments' },
            ].map((exp) => (
              <div key={exp.entity} className="p-4 bg-white/5 rounded-xl border border-white/10 space-y-3">
                <div>
                  <h3 className="font-bold text-white text-sm">{exp.title}</h3>
                  <p className="text-gray-400 text-[11px] mt-1">{exp.desc}</p>
                </div>
                <div className="flex gap-2 pt-2 border-t border-white/5">
                  <button
                    onClick={() => handleExport('excel', exp.entity)}
                    className="flex-1 flex items-center justify-center gap-1 py-1.5 rounded bg-gold-500 text-black font-semibold hover:bg-gold-400 transition-colors"
                  >
                    <FileSpreadsheet className="w-3.5 h-3.5" /> Excel (.xlsx)
                  </button>
                  <button
                    onClick={() => handleExport('word', exp.entity)}
                    className="flex-1 flex items-center justify-center gap-1 py-1.5 rounded bg-white/10 text-white font-semibold hover:bg-white/20 transition-colors border border-white/10"
                  >
                    <FileText className="w-3.5 h-3.5" /> Word (.doc)
                  </button>
                </div>
              </div>
            ))}
          </div>
        </div>
      )}

      {/* ADD PRODUCT MODAL */}
      {showAddProductModal && (
        <div className="fixed inset-0 z-50 bg-black/80 backdrop-blur-sm flex items-center justify-center p-4">
          <div className="glass-card max-w-lg w-full p-6 rounded-2xl border border-gold-500/30 space-y-6">
            <div className="flex items-center justify-between border-b border-white/10 pb-4">
              <h2 className="text-lg font-bold text-white font-serif">Add New Product to Catalog</h2>
              <button
                onClick={() => setShowAddProductModal(false)}
                className="text-gray-400 hover:text-white text-sm"
              >
                ✕
              </button>
            </div>

            <form onSubmit={handleAddProduct} className="space-y-4 text-xs">
              <div className="space-y-1">
                <label className="text-gray-300 font-medium">Product Title</label>
                <input
                  type="text"
                  placeholder="e.g. Royal Emerald Pendant Gold Necklace"
                  value={newTitle}
                  onChange={(e) => setNewTitle(e.target.value)}
                  className="w-full bg-white/5 border border-white/10 rounded-lg px-3 py-2 text-white placeholder-gray-500 focus:outline-none focus:border-gold-500"
                  required
                />
              </div>

              <div className="grid grid-cols-2 gap-4">
                <div className="space-y-1">
                  <label className="text-gray-300 font-medium">Category</label>
                  <select
                    value={newCategory}
                    onChange={(e) => setNewCategory(e.target.value)}
                    className="w-full bg-black border border-white/10 rounded-lg px-3 py-2 text-white focus:outline-none focus:border-gold-500"
                  >
                    <option value="Bridal Jewellery">Bridal Jewellery</option>
                    <option value="Solitaire Rings">Solitaire Rings</option>
                    <option value="Luxury Skincare">Luxury Skincare</option>
                    <option value="Cosmetics">Cosmetics</option>
                  </select>
                </div>
                <div className="space-y-1">
                  <label className="text-gray-300 font-medium">SKU Code</label>
                  <input
                    type="text"
                    placeholder="JWL-EMR-009"
                    value={newSku}
                    onChange={(e) => setNewSku(e.target.value)}
                    className="w-full bg-white/5 border border-white/10 rounded-lg px-3 py-2 text-white placeholder-gray-500 focus:outline-none focus:border-gold-500"
                  />
                </div>
              </div>

              <div className="grid grid-cols-2 gap-4">
                <div className="space-y-1">
                  <label className="text-gray-300 font-medium">Base Price (₹)</label>
                  <input
                    type="number"
                    placeholder="125000"
                    value={newPrice}
                    onChange={(e) => setNewPrice(e.target.value)}
                    className="w-full bg-white/5 border border-white/10 rounded-lg px-3 py-2 text-white placeholder-gray-500 focus:outline-none focus:border-gold-500"
                    required
                  />
                </div>
                <div className="space-y-1">
                  <label className="text-gray-300 font-medium">Initial Stock Qty</label>
                  <input
                    type="number"
                    placeholder="10"
                    value={newStock}
                    onChange={(e) => setNewStock(e.target.value)}
                    className="w-full bg-white/5 border border-white/10 rounded-lg px-3 py-2 text-white placeholder-gray-500 focus:outline-none focus:border-gold-500"
                  />
                </div>
              </div>

              <div className="space-y-1">
                <label className="text-gray-300 font-medium">Hallmark / Purity Badge</label>
                <input
                  type="text"
                  placeholder="22K Hallmarked Gold / VVS1 Certified"
                  value={newPurity}
                  onChange={(e) => setNewPurity(e.target.value)}
                  className="w-full bg-white/5 border border-white/10 rounded-lg px-3 py-2 text-white placeholder-gray-500 focus:outline-none focus:border-gold-500"
                />
              </div>

              <button
                type="submit"
                className="w-full py-3 rounded-lg bg-gold-500 text-black font-bold uppercase tracking-wider hover:bg-gold-400 transition-colors mt-4"
              >
                Create & Save Product
              </button>
            </form>
          </div>
        </div>
      )}
    </div>
  );
}
