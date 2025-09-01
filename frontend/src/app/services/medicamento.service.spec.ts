import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule } from '@angular/common/http/testing';

import { MedicamentoService } from './medicamento.service';

describe('MedicamentoService', () => {
  let service: MedicamentoService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule]
    });
    service = TestBed.inject(MedicamentoService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('should have getMedicamentos method', () => {
    expect(service.getMedicamentos).toBeDefined();
  });
});
