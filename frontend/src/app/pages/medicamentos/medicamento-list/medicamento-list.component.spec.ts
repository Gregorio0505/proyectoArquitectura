import { ComponentFixture, TestBed } from '@angular/core/testing';
import { RouterTestingModule } from '@angular/router/testing';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { provideMockStore } from '@ngrx/store/testing';

import { MedicamentoListComponent } from './medicamento-list.component';

describe('MedicamentoListComponent', () => {
  let component: MedicamentoListComponent;
  let fixture: ComponentFixture<MedicamentoListComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [
        MedicamentoListComponent,
        RouterTestingModule,
        HttpClientTestingModule
      ],
      providers: [
        provideMockStore({
          initialState: {
            medicamentos: { 
              list: [
                { id: 1, nombre: 'Test Medicamento 1', precio: 10.99, stock: 100 },
                { id: 2, nombre: 'Test Medicamento 2', precio: 15.99, stock: 50 }
              ], 
              loading: false,
              error: null
            }
          }
        })
      ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(MedicamentoListComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should have list content', () => {
    const compiled = fixture.nativeElement;
    expect(compiled).toBeTruthy();
  });
});
