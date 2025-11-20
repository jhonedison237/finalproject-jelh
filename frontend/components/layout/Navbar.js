'use client';

import { Menu } from 'lucide-react';

/**
 * Barra de navegación superior
 */
export const Navbar = ({ onMenuClick }) => {
  return (
    <nav className="bg-white border-b-2 border-gray-300 fixed top-0 left-0 right-0 z-40 shadow-md">
      <div className="px-4 sm:px-6 lg:px-8">
        <div className="flex items-center justify-between h-16">
          {/* Logo y menú */}
          <div className="flex items-center gap-4">
            <button
              onClick={onMenuClick}
              className="lg:hidden p-2 rounded-md text-gray-600 hover:bg-gray-100"
            >
              <Menu className="h-6 w-6" />
            </button>
            <div className="flex items-center gap-2">
              <div className="bg-gradient-to-br from-primary-600 to-primary-700 text-white font-bold text-xl px-3 py-1.5 rounded-lg shadow-lg">
                💰
              </div>
              <h1 className="text-xl font-bold text-gray-900 tracking-tight">
                ExpenseTracker
              </h1>
            </div>
          </div>

          {/* Usuario */}
          <div className="flex items-center gap-4">
            <div className="text-right hidden sm:block">
              <p className="text-sm font-semibold text-gray-900">Usuario Demo</p>
              <p className="text-xs text-gray-600">demo@expensetracker.com</p>
            </div>
            <div className="h-10 w-10 rounded-full bg-gradient-to-br from-primary-600 to-primary-700 flex items-center justify-center text-white font-bold shadow-md ring-2 ring-primary-200">
              DU
            </div>
          </div>
        </div>
      </div>
    </nav>
  );
};

