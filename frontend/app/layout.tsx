import type { Metadata } from "next";
import "./globals.css";

export const metadata: Metadata = {
  title: "ExpenseTracker - Gestiona tus finanzas personales",
  description: "Aplicación web para gestionar ingresos, gastos y presupuestos personales",
};

export default function RootLayout({
  children,
}: Readonly<{
  children: React.ReactNode;
}>) {
  return (
    <html lang="es">
      <body className="antialiased">
        {children}
      </body>
    </html>
  );
}
