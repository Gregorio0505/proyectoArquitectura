export interface Medicamento {
  idMedicamento: number;
  nombre: string;
  principioActivo: string;
  descripcion: string;
  laboratorio: string;
  fotoUrl: string;
  precio: number;
  marca: string;
  stock: number;
  requiereReceta: boolean | string;
}
