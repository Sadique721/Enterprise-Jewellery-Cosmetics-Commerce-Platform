'use client';

import React, { useState, useEffect } from 'react';
import { User, Package, MapPin, Heart, Shield, LogOut, Trash2, ShoppingBag, Plus, CheckCircle2 } from 'lucide-react';
import Link from 'next/link';
import { useWishlistStore } from '@/store/useWishlistStore';
import { useCartStore } from '@/store/useCartStore';

export default function AccountPage() {
  const [activeTab, setActiveTab] = useState<'profile' | 'orders' | 'addresses' | 'wishlist'>('profile');
  const { items: wishlistItems, removeItem: removeFromWishlist } = useWishlistStore();
  const addToCart = useCartStore((state) => state.addItem);

  const [userProfile, setUserProfile] = useState({
    name: 'Sadique Amin',
    email: 'mdsadiqueamin721721@gmail.com',
    phone: '+91 98765 43211',
    membership: 'SANAB Royal Privilege (Gold tier)',
    joinedDate: 'July 2026',
  });

  useEffect(() => {
    try {
      const stored = localStorage.getItem('sanab_current_user');
      if (stored) {
        const parsed = JSON.parse(stored);
        if (parsed.email) {
          setUserProfile({
            name: parsed.name || 'Royal Member',
            email: parsed.email,
            phone: parsed.phone || '+91 98765 43210',
            membership: parsed.membership || 'SANAB Royal Privilege (Gold tier)',
            joinedDate: 'July 2026',
          });
        }
      }
    } catch (err) {
      console.warn('Failed to parse current user session:', err);
    }
  }, []);

  const getInitials = (nameStr: string) => {
    const parts = nameStr.trim().split(' ');
    if (parts.length >= 2) {
      return (parts[0][0] + parts[1][0]).toUpperCase();
    }
    return nameStr.substring(0, 2).toUpperCase();
  };

  const sampleOrders = [
    {
      id: 'SNB-ORD-9021',
      date: '27 July 2026',
      total: '₹1,85,000',
      status: 'PAID',
      items: ['Royal Heritage Kundan Choker Necklace (22K Gold)'],
    },
    {
      id: 'SNB-ORD-9018',
      date: '20 July 2026',
      total: '₹11,700',
      status: 'DELIVERED',
      items: ['24K Gold Regenerative Serum', 'Velvet Matte Royal Ruby Lipstick'],
    },
  ];

  const sampleAddresses = [
    {
      id: '1',
      type: 'Home (Default)',
      name: userProfile.name,
      line: 'Flat 402, Royal Crest Apartments, Bandra West',
      city: 'Mumbai',
      state: 'Maharashtra',
      pincode: '400050',
      phone: userProfile.phone,
      isDefault: true,
    },
    {
      id: '2',
      type: 'Office',
      name: `${userProfile.name} (Corporate)`,
      line: 'Suite 1204, BKC Financial Tower, BKC',
      city: 'Mumbai',
      state: 'Maharashtra',
      pincode: '400051',
      phone: userProfile.phone,
      isDefault: false,
    },
  ];

  return (
    <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-12 space-y-10">
      {/* Account Header */}
      <div className="flex flex-col md:flex-row md:items-center justify-between gap-6 border-b border-white/10 pb-8">
        <div className="flex items-center gap-4">
          <div className="w-16 h-16 rounded-full bg-gold-500/20 border border-gold-500/40 flex items-center justify-center text-gold-400 text-2xl font-serif font-bold">
            {getInitials(userProfile.name)}
          </div>
          <div>
            <div className="flex items-center gap-2">
              <h1 className="text-2xl font-bold text-white font-serif">{userProfile.name}</h1>
              <span className="px-2.5 py-0.5 rounded-full bg-gold-500/20 text-gold-400 text-[10px] font-bold border border-gold-500/30 uppercase tracking-wider">
                VIP Customer
              </span>
            </div>
            <p className="text-xs text-gray-400 mt-1">{userProfile.email} • {userProfile.membership}</p>
          </div>
        </div>

        <div className="flex items-center gap-3">
          <Link
            href="/admin"
            className="flex items-center gap-1.5 px-4 py-2 rounded-lg bg-gold-500/10 text-gold-400 border border-gold-500/30 text-xs font-semibold hover:bg-gold-500/20 transition-colors"
          >
            <Shield className="w-4 h-4" />
            Switch to Admin Console
          </Link>
          <button
            onClick={() => {
              localStorage.removeItem('sanab_current_user');
              window.location.href = '/login';
            }}
            className="flex items-center gap-1.5 px-4 py-2 rounded-lg bg-white/5 text-gray-300 border border-white/10 text-xs font-semibold hover:bg-white/10 transition-colors"
          >
            <LogOut className="w-4 h-4" />
            Sign Out
          </button>
        </div>
      </div>

      {/* Main Grid: Sidebar Tabs & Tab Content */}
      <div className="grid grid-cols-1 md:grid-cols-4 gap-8">
        {/* Navigation Sidebar */}
        <div className="space-y-2">
          {[
            { id: 'profile', label: 'My Profile', icon: User },
            { id: 'orders', label: 'Order History', icon: Package },
            { id: 'addresses', label: 'Saved Addresses', icon: MapPin },
            { id: 'wishlist', label: `My Wishlist (${wishlistItems.length})`, icon: Heart },
          ].map((tab) => {
            const Icon = tab.icon;
            const isActive = activeTab === tab.id;
            return (
              <button
                key={tab.id}
                onClick={() => setActiveTab(tab.id as any)}
                className={`w-full flex items-center gap-3 px-4 py-3 rounded-xl text-xs font-semibold transition-all ${
                  isActive
                    ? 'bg-gold-500 text-black shadow-lg shadow-gold-500/10'
                    : 'glass-card text-gray-300 hover:text-white hover:bg-white/10'
                }`}
              >
                <Icon className="w-4 h-4" />
                {tab.label}
              </button>
            );
          })}
        </div>

        {/* Tab Content Area */}
        <div className="md:col-span-3">
          {/* PROFILE TAB */}
          {activeTab === 'profile' && (
            <div className="glass-card p-6 rounded-xl space-y-6">
              <h2 className="text-lg font-bold text-white font-serif border-b border-white/10 pb-4">Personal Information</h2>
              <div className="grid grid-cols-1 sm:grid-cols-2 gap-6 text-xs">
                <div className="space-y-1">
                  <span className="text-gray-400">Full Name</span>
                  <p className="text-white font-medium text-sm">{userProfile.name}</p>
                </div>
                <div className="space-y-1">
                  <span className="text-gray-400">Email Address</span>
                  <p className="text-white font-medium text-sm">{userProfile.email}</p>
                </div>
                <div className="space-y-1">
                  <span className="text-gray-400">Phone Number</span>
                  <p className="text-white font-medium text-sm">{userProfile.phone}</p>
                </div>
                <div className="space-y-1">
                  <span className="text-gray-400">Account Tier</span>
                  <p className="text-gold-400 font-medium text-sm">{userProfile.membership}</p>
                </div>
              </div>
            </div>
          )}

          {/* ORDERS TAB */}
          {activeTab === 'orders' && (
            <div className="glass-card p-6 rounded-xl space-y-6">
              <h2 className="text-lg font-bold text-white font-serif border-b border-white/10 pb-4">
                Recent Orders ({sampleOrders.length})
              </h2>

              <div className="space-y-4">
                {sampleOrders.map((order) => (
                  <div key={order.id} className="p-4 bg-white/5 rounded-xl border border-white/10 space-y-3">
                    <div className="flex items-center justify-between text-xs">
                      <div>
                        <span className="font-mono text-gold-300 font-bold">{order.id}</span>
                        <span className="text-gray-400 ml-3">{order.date}</span>
                      </div>
                      <span className="px-2.5 py-0.5 rounded text-[10px] font-bold bg-emerald-500/20 text-emerald-400 border border-emerald-500/30">
                        {order.status}
                      </span>
                    </div>
                    <div className="text-xs text-white">
                      {order.items.map((item, idx) => (
                        <p key={idx} className="font-medium">{item}</p>
                      ))}
                    </div>
                    <div className="flex items-center justify-between text-xs border-t border-white/5 pt-2">
                      <span className="text-gray-400">Total Amount</span>
                      <span className="text-gold-400 font-bold text-sm">{order.total}</span>
                    </div>
                  </div>
                ))}
              </div>
            </div>
          )}

          {/* ADDRESSES TAB */}
          {activeTab === 'addresses' && (
            <div className="glass-card p-6 rounded-xl space-y-6">
              <div className="flex items-center justify-between border-b border-white/10 pb-4">
                <h2 className="text-lg font-bold text-white font-serif">Saved Addresses</h2>
                <button className="flex items-center gap-1.5 px-3 py-1.5 rounded-lg bg-gold-500 text-black text-xs font-semibold hover:bg-gold-400 transition-colors">
                  <Plus className="w-3.5 h-3.5" /> Add New Address
                </button>
              </div>

              <div className="grid grid-cols-1 sm:grid-cols-2 gap-4 text-xs">
                {sampleAddresses.map((addr) => (
                  <div key={addr.id} className={`p-4 rounded-xl border space-y-2 ${addr.isDefault ? 'bg-gold-500/10 border-gold-500/40' : 'bg-white/5 border-white/10'}`}>
                    <div className="flex items-center justify-between">
                      <span className="font-semibold text-white">{addr.type}</span>
                      {addr.isDefault && (
                        <span className="flex items-center gap-1 text-[10px] text-gold-400 font-bold">
                          <CheckCircle2 className="w-3 h-3" /> Default
                        </span>
                      )}
                    </div>
                    <p className="text-gray-300 font-medium">{addr.name}</p>
                    <p className="text-gray-400">{addr.line}, {addr.city}, {addr.state} - {addr.pincode}</p>
                    <p className="text-gray-400 font-mono">{addr.phone}</p>
                  </div>
                ))}
              </div>
            </div>
          )}

          {/* WISHLIST TAB */}
          {activeTab === 'wishlist' && (
            <div className="glass-card p-6 rounded-xl space-y-6">
              <div className="flex items-center justify-between border-b border-white/10 pb-4">
                <h2 className="text-lg font-bold text-white font-serif">
                  Saved Wishlist Items ({wishlistItems.length})
                </h2>
                <Link href="/catalog" className="text-xs text-gold-400 hover:underline">
                  Browse Catalog
                </Link>
              </div>

              {wishlistItems.length === 0 ? (
                <div className="text-center py-12 space-y-3">
                  <Heart className="w-10 h-10 text-gray-500 mx-auto" />
                  <p className="text-gray-400 text-xs">Your wishlist is empty.</p>
                </div>
              ) : (
                <div className="grid grid-cols-1 sm:grid-cols-2 gap-4">
                  {wishlistItems.map((item) => (
                    <div key={item.id} className="p-4 bg-white/5 rounded-xl border border-white/10 flex gap-4">
                      <img src={item.imageUrl} alt={item.name} className="w-20 h-20 object-cover rounded-lg bg-black/40" />
                      <div className="flex-1 flex flex-col justify-between text-xs">
                        <div>
                          <h3 className="font-serif font-semibold text-white line-clamp-1">{item.name}</h3>
                          <p className="text-gold-400 font-bold font-serif text-sm mt-1">₹{item.price.toLocaleString('en-IN')}</p>
                        </div>

                        <div className="flex items-center gap-2 mt-2">
                          <button
                            onClick={() => {
                              addToCart({
                                productId: item.id,
                                sku: item.id,
                                productName: item.name,
                                imageUrl: item.imageUrl,
                                unitPrice: item.price,
                                quantity: 1,
                              });
                              removeFromWishlist(item.id);
                            }}
                            className="flex-1 py-1.5 bg-gold-500 text-black font-semibold rounded text-[11px] flex items-center justify-center gap-1 hover:bg-gold-400"
                          >
                            <ShoppingBag className="w-3 h-3" /> Move to Bag
                          </button>
                          <button
                            onClick={() => removeFromWishlist(item.id)}
                            className="p-1.5 bg-white/5 text-gray-400 hover:text-rose-400 rounded"
                          >
                            <Trash2 className="w-4 h-4" />
                          </button>
                        </div>
                      </div>
                    </div>
                  ))}
                </div>
              )}
            </div>
          )}
        </div>
      </div>
    </div>
  );
}
