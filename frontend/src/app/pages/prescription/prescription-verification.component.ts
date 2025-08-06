import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';

@Component({
  selector: 'app-prescription-verification',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './prescription-verification.component.html',
  styleUrls: ['./prescription-verification.component.css']
})
export class PrescriptionVerificationComponent {
  prescriptionCode: string = '';
  message: string = '';
  isError: boolean = false;

  constructor() {}

  verifyPrescription(): void {
    if (!this.prescriptionCode) {
      this.message = 'Por favor ingrese un código de receta';
      this.isError = true;
      return;
    }

    this.message = `Verificando receta con código: ${this.prescriptionCode}`;
    this.isError = false;
  }
}
