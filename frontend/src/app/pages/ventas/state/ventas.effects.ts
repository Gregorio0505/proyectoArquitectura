import { Injectable, inject } from '@angular/core';
import { Actions, createEffect, ofType } from '@ngrx/effects';
import * as VentasActions from './ventas.actions';
import { mergeMap, map, catchError } from 'rxjs/operators';
import { of } from 'rxjs';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { environment } from '../../../../environments/environment';

@Injectable()
export class VentasEffects {
  private actions$ = inject(Actions);
  private http = inject(HttpClient);

  private baseUrl = `${environment.apiUrl}/api/ventas`;

  loadVentas$ = createEffect(() =>
    this.actions$.pipe(
      ofType(VentasActions.loadVentas),
      mergeMap(() => {
        console.log('VentasEffects: Loading ventas from', this.baseUrl);
        const token = localStorage.getItem('token');
        console.log('VentasEffects: Token exists:', !!token);
        
        // Create headers with the token
        const headers = new HttpHeaders({
          'Content-Type': 'application/json',
          'Authorization': `Bearer ${token}`
        });
        
        console.log('VentasEffects: Request headers set with token');
        
        // Use headers in the request
        return this.http.get<any[]>(this.baseUrl, { headers }).pipe(
          map(ventas => {
            console.log('VentasEffects: Ventas loaded successfully', ventas);
            return VentasActions.loadVentasSuccess({ ventas });
          }),
          catchError(error => {
            console.error('VentasEffects: Error loading ventas', error);
            return of(VentasActions.loadVentasFailure({ error }));
          })
        );
      })
    )
  );

  loadVentaDetail$ = createEffect(() =>
    this.actions$.pipe(
      ofType(VentasActions.loadVentaDetail),
      mergeMap(({ id }) => {
        console.log(`VentasEffects: Loading venta detail for ID ${id}`);
        const token = localStorage.getItem('token');
        
        // Create headers with the token
        const headers = new HttpHeaders({
          'Content-Type': 'application/json',
          'Authorization': `Bearer ${token}`
        });
        
        return this.http.get<any>(`${this.baseUrl}/${id}`, { headers }).pipe(
          map(venta => VentasActions.loadVentaDetailSuccess({ venta })),
          catchError(error => {
            console.error(`VentasEffects: Error loading venta detail for ID ${id}`, error);
            return of(VentasActions.loadVentaDetailFailure({ error }));
          })
        );
      })
    )
  );
}



