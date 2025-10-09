import http from 'k6/http';
import { check, sleep } from 'k6';
import { textSummary } from 'https://jslib.k6.io/k6-summary/0.0.4/index.js';

export const options = {
  stages: [
    { duration: '20s', target: 20 },   
    { duration: '1m',  target: 50 },  
    { duration: '20s', target: 0 },    
  ],
  thresholds: {
    http_req_failed: ['rate<0.01'],     
    http_req_duration: ['p(95)<500'],   
  },
};

const FRONT_URL = __ENV.FRONT_URL || 'http://host.docker.internal:4302';

export default function () {
  const res = http.get(`${FRONT_URL}/`, { redirects: 0 }); 
  check(res, {
    'frontend / 200/304': (r) => r.status === 200 || r.status === 304,
    'content-type text/html': (r) =>
      (r.headers['Content-Type'] || '').includes('text/html'),
  });

  sleep(1);
}

export function handleSummary(data) {
  return {
    stdout: textSummary(data, { indent: ' ', enableColors: true }),
    'results/summary_front.json': JSON.stringify(data, null, 2),
  };
}
