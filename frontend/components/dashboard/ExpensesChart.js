'use client';

import { PieChart, Pie, Cell, ResponsiveContainer, Legend, Tooltip } from 'recharts';
import { Card, CardHeader, CardTitle, CardBody } from '../common/Card';
import { CHART_COLORS } from '@/lib/utils/constants';
import { formatCurrency } from '@/lib/utils/formatters';

/**
 * Componente para mostrar gráfico de gastos por categoría
 */
export const ExpensesChart = ({ data = [] }) => {
  // Validar que data sea un array
  const safeData = Array.isArray(data) ? data : [];
  
  // Transformar datos para recharts
  const chartData = safeData.map((item, index) => ({
    name: item.categoryName,
    value: Math.abs(item.totalAmount), // Convertir a positivo para el gráfico
    percentage: item.percentage,
    color: CHART_COLORS[index % CHART_COLORS.length],
  }));

  // Custom tooltip
  const CustomTooltip = ({ active, payload }) => {
    if (active && payload && payload.length) {
      const data = payload[0].payload;
      return (
        <div className="bg-white p-3 border border-gray-200 rounded-lg shadow-lg">
          <p className="font-medium text-gray-900">{data.name}</p>
          <p className="text-sm text-gray-600">
            {formatCurrency(data.value)}
          </p>
          <p className="text-xs text-gray-500">
            {data.percentage}% del total
          </p>
        </div>
      );
    }
    return null;
  };

  if (!safeData || safeData.length === 0) {
    return (
      <Card>
        <CardHeader>
          <CardTitle>Gastos por Categoría</CardTitle>
        </CardHeader>
        <CardBody>
          <div className="flex items-center justify-center h-64 text-gray-500">
            No hay datos para mostrar
          </div>
        </CardBody>
      </Card>
    );
  }

  return (
    <Card>
      <CardHeader>
        <CardTitle>Gastos por Categoría</CardTitle>
      </CardHeader>
      <CardBody>
        <ResponsiveContainer width="100%" height={300}>
          <PieChart>
            <Pie
              data={chartData}
              cx="50%"
              cy="50%"
              labelLine={false}
              label={({ name, percentage }) => `${name} (${percentage}%)`}
              outerRadius={80}
              fill="#8884d8"
              dataKey="value"
            >
              {chartData.map((entry, index) => (
                <Cell key={`cell-${index}`} fill={entry.color} />
              ))}
            </Pie>
            <Tooltip content={<CustomTooltip />} />
            <Legend />
          </PieChart>
        </ResponsiveContainer>

        {/* Lista de categorías */}
        <div className="mt-6 space-y-2">
          {chartData.map((item, index) => (
            <div key={index} className="flex items-center justify-between">
              <div className="flex items-center gap-2">
                <div
                  className="w-3 h-3 rounded-full"
                  style={{ backgroundColor: item.color }}
                />
                <span className="text-sm text-gray-700">{item.name}</span>
              </div>
              <div className="text-sm font-medium text-gray-900">
                {formatCurrency(item.value)} ({item.percentage}%)
              </div>
            </div>
          ))}
        </div>
      </CardBody>
    </Card>
  );
};

