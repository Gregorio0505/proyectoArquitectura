import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { RouterTestingModule } from '@angular/router/testing';
import { ActivatedRoute } from '@angular/router';
import { of } from 'rxjs';
import { provideMockStore, MockStore } from '@ngrx/store/testing';

// Mock ActivatedRoute
export const mockActivatedRoute = {
  snapshot: {
    paramMap: {
      get: (key: string) => '1',
      has: (key: string) => true
    },
    queryParamMap: {
      get: (key: string) => null,
      has: (key: string) => false
    }
  },
  params: of({ id: '1' }),
  queryParams: of({}),
  fragment: of(null),
  data: of({}),
  url: of([]),
  outlet: 'primary',
  component: null,
  routeConfig: null,
  root: null,
  parent: null,
  firstChild: null,
  children: [],
  pathFromRoot: [],
  paramMap: of(new Map([['id', '1']])),
  queryParamMap: of(new Map()),
  toString: () => 'ActivatedRoute'
};

// Mock Store
export const mockStore = {
  select: jasmine.createSpy('select').and.returnValue(of({})),
  dispatch: jasmine.createSpy('dispatch'),
  pipe: jasmine.createSpy('pipe').and.returnValue(of({}))
};

// Configuración global de testing
export const configureTestingModule = () => {
  return TestBed.configureTestingModule({
    imports: [
      HttpClientTestingModule,
      RouterTestingModule
    ],
    providers: [
      { provide: ActivatedRoute, useValue: mockActivatedRoute },
      provideMockStore({
        initialState: {
          auth: { user: null, isAuthenticated: false },
          medicamentos: { items: [], loading: false },
          carrito: { items: [], total: 0 }
        }
      })
    ]
  });
};

// Helper para crear mocks de servicios
export const createMockService = (serviceClass: any) => {
  const mock = {};
  const methods = Object.getOwnPropertyNames(serviceClass.prototype)
    .filter(name => name !== 'constructor');
  
  methods.forEach(method => {
    mock[method] = jasmine.createSpy(method);
  });
  
  return mock;
};
