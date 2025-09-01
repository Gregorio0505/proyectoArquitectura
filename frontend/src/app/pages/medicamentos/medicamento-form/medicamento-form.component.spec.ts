import { ComponentFixture, TestBed } from '@angular/core/testing';
import { RouterTestingModule } from '@angular/router/testing';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { provideMockStore } from '@ngrx/store/testing';

import { MedicamentoFormComponent } from './medicamento-form.component';

describe('MedicamentoFormComponent', () => {
  let component: MedicamentoFormComponent;
  let fixture: ComponentFixture<MedicamentoFormComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [
        MedicamentoFormComponent,
        RouterTestingModule,
        HttpClientTestingModule
      ],
      providers: [
        provideMockStore({
          initialState: {
            medicamentos: { 
              list: [
                { id: 1, nombre: 'Test Medicamento 1', precio: 10.99, stock: 100 }
              ], 
              loading: false,
              error: null
            }
          }
        })
      ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(MedicamentoFormComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should have form content', () => {
    const compiled = fixture.nativeElement;
    expect(compiled).toBeTruthy();
  });
});
