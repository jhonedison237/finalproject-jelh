'use client';

import Link from 'next/link';
import { usePathname } from 'next/navigation';
import { Home, CreditCard, X } from 'lucide-react';
import { cn } from '@/lib/utils/helpers';

/**
 * Barra lateral de navegación
 */
export const Sidebar = ({ isOpen, onClose }) => {
  const pathname = usePathname();

  const menuItems = [
    {
      name: 'Dashboard',
      href: '/',
      icon: Home,
    },
    {
      name: 'Transacciones',
      href: '/transactions',
      icon: CreditCard,
    },
  ];

  return (
    <>
      {/* Overlay para móvil */}
      {isOpen && (
        <div
          className="fixed inset-0 bg-black bg-opacity-50 z-40 lg:hidden"
          onClick={onClose}
        />
      )}

      {/* Sidebar */}
      <aside
        className={cn(
          'fixed top-16 left-0 bottom-0 w-64 bg-white border-r-2 border-gray-300 z-40 transition-transform duration-300 shadow-lg',
          'lg:translate-x-0',
          isOpen ? 'translate-x-0' : '-translate-x-full'
        )}
      >
        {/* Botón cerrar (solo móvil) */}
        <button
          onClick={onClose}
          className="lg:hidden absolute top-4 right-4 p-2 rounded-md text-gray-600 hover:bg-gray-100"
        >
          <X className="h-5 w-5" />
        </button>

        {/* Menú de navegación */}
        <nav className="p-4 space-y-2">
          {menuItems.map((item) => {
            const Icon = item.icon;
            const isActive = pathname === item.href;

            return (
              <Link
                key={item.href}
                href={item.href}
                onClick={() => {
                  // Cerrar sidebar en móvil al hacer clic
                  if (window.innerWidth < 1024) {
                    onClose();
                  }
                }}
                className={cn(
                  'flex items-center gap-3 px-4 py-3 rounded-lg transition-all duration-200',
                  isActive
                    ? 'bg-gradient-to-r from-blue-600 to-blue-700 text-white font-bold shadow-lg border-l-4 border-blue-800'
                    : 'text-gray-700 hover:bg-gray-100 hover:text-gray-900 font-medium'
                )}
              >
                <Icon className="h-5 w-5" />
                <span>{item.name}</span>
              </Link>
            );
          })}
        </nav>

        {/* Footer */}
        <div className="absolute bottom-0 left-0 right-0 p-4 border-t-2 border-gray-300 bg-gradient-to-r from-gray-50 to-gray-100">
          <div className="flex items-center gap-3 mb-3 p-2 bg-white rounded-lg shadow-sm">
            <div className="h-10 w-10 rounded-full bg-gradient-to-br from-blue-600 to-blue-700 flex items-center justify-center text-white font-bold text-sm shadow-md">
              DU
            </div>
            <div className="flex-1 min-w-0">
              <p className="text-sm font-semibold text-gray-900 truncate">Usuario Demo</p>
              <p className="text-xs text-gray-600 truncate">demo@expensetracker.com</p>
            </div>
          </div>
          <div className="text-xs text-gray-500 text-center">
            <p className="font-medium">ExpenseTracker v1.0</p>
            <p className="mt-1">© 2024</p>
          </div>
        </div>
      </aside>
    </>
  );
};

