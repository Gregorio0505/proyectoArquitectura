import http from 'k6/http';
import { check, sleep } from 'k6';
import { textSummary } from 'https://jslib.k6.io/k6-summary/0.0.4/index.js';

export const options = {
  stages: [
    { duration: '30s', target: 200 },
    { duration: '30s', target: 400 },
    { duration: '30s', target: 700 },
    { duration: '30s', target: 1000 },
    { duration: '2m',  target: 1000 },
    { duration: '30s', target: 0 },
  ],
  thresholds: {
    http_req_failed:   ['rate<0.05'],    // acepta hasta 5% de errores en estrés
    http_req_duration: ['p(95)<500'],   // p95 < 1.5s en carga
  },
  // Opcional: más throughput por conexión
  // insecureSkipTLSVerify: true,
};

const BASE_URL = __ENV.BASE_URL || 'http://host.docker.internal:8084';

// Si necesitas token, descomenta y ajusta:
// function getToken() {
//   const res = http.post(`${BASE_URL}/auth/login`, JSON.stringify({ username: 'admin', password: 'admin' }), { headers: { 'Content-Type': 'application/json' }});
//   return res.json('token');
// }
// const TOKEN = __ENV.TOKEN || getToken();
// const HEADERS = { headers: { Authorization: `Bearer ${TOKEN}` } };

export default function () {
  // Endpoints reales del backend (ajusta a los tuyos)
  const r1 = http.get(`${BASE_URL}/api/products`);            // GET pesado (DB)
  check(r1, { 'products 2xx/3xx': (r) => r.status >= 200 && r.status < 400 });

  // Si tienes POST para cargar más CPU/DB, descomenta:
  // const payload = JSON.stringify({ name: 'load', quantity: 1 });
  // const r2 = http.post(`${BASE_URL}/api/orders`, payload, { headers: { 'Content-Type': 'application/json' } });
  // check(r2, { 'orders 2xx/3xx': (r) => r.status >= 200 && r.status < 400 });

  // QUITA o reduce el sleep para más RPS; con 1000 VUs sleep(1) ≈ ~1000 req/s
  sleep(1);
}

export function handleSummary(data) {
  return {
    stdout: textSummary(data, { indent: ' ', enableColors: true }),
    'results/summary_backend.json': JSON.stringify(data, null, 2),
  };
}
