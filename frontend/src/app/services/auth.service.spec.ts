import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { RouterTestingModule } from '@angular/router/testing';
import { provideMockStore } from '@ngrx/store/testing';

import { AuthService } from './auth.service';

describe('AuthService', () => {
  let service: AuthService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [
        HttpClientTestingModule,
        RouterTestingModule
      ],
      providers: [
        provideMockStore({
          initialState: {
            auth: { user: null, isAuthenticated: false }
          }
        })
      ]
    });
    service = TestBed.inject(AuthService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('should have login method', () => {
    expect(service.login).toBeDefined();
  });

  it('should have register method', () => {
    expect(service.register).toBeDefined();
  });

  it('should have logout method', () => {
    expect(service.logout).toBeDefined();
  });
});
