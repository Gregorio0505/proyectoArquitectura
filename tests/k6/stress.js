import http from 'k6/http';
import { check, sleep } from 'k6';
import { textSummary } from 'https://jslib.k6.io/k6-summary/0.0.4/index.js';

export const options = {
  stages: [
    { duration: '30s', target: 20 },   
    { duration: '2m',  target: 100 },  
    { duration: '3m',  target: 100 },  
    { duration: '30s', target: 0 },    
  ],
  thresholds: {
    http_req_failed: ['rate<0.01'],         
    http_req_duration: ['p(95)<800'],        
  },
};

const BASE_URL = __ENV.BASE_URL || 'http://localhost:8084'; 

export default function () {
  // 1️⃣ Endpoint liviano
  let health = http.get(`${BASE_URL}/health`);
  check(health, { '✅ /health 200 OK': (r) => r.status === 200 });

  // 2️⃣ Endpoint de productos
  let products = http.get(`${BASE_URL}/api/products`);
  check(products, { '✅ /api/products 200 OK': (r) => r.status === 200 });

  // 3️⃣ Endpoint de órdenes (si existe)
  let orders = http.get(`${BASE_URL}/api/orders`);
  check(orders, { '✅ /api/orders 200 OK': (r) => r.status === 200 });

  // 4️⃣ (opcional) Ejemplo POST si quieres probar escritura:  
  // const payload = JSON.stringify({ name: 'test', quantity: 1 });
  // const headers = { headers: { 'Content-Type': 'application/json' } };
  // let postOrder = http.post(`${BASE_URL}/api/orders`, payload, headers);
  // check(postOrder, { '✅ /api/orders POST 201 Created': (r) => r.status === 201 });

  sleep(1); // pausa corta entre iteraciones
}

export function handleSummary(data) {
  return {
    stdout: textSummary(data, { indent: ' ', enableColors: true }),
    'tests/k6/results/summary.json': JSON.stringify(data, null, 2),
  };
}
