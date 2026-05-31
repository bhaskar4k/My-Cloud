import { Component } from '@angular/core';
import {
  FormControl,
  FormGroup,
  Validators,
  ReactiveFormsModule
} from '@angular/forms';
import { MatDialog } from '@angular/material/dialog';
import { CustomAlertComponent } from '../../common-components/custom-alert/custom-alert.component';
import { ResponseTypeColor } from '../../constants/commonConsts';
import { AuthService } from '../../services/auth.service';
import { ApiResponseDto } from '../../models/dto.model';

@Component({
  selector: 'app-register',
  imports: [
    ReactiveFormsModule
  ],
  templateUrl: './register.component.html',
  styleUrl: './register.component.css'
})
export class RegisterComponent {
  registerForm = new FormGroup({
    name: new FormControl('', [
      Validators.required,
      Validators.minLength(3)
    ]),

    email: new FormControl('', [
      Validators.required,
      Validators.email
    ]),

    phone: new FormControl('', [
      Validators.required,
      Validators.pattern('^[0-9]{10}$')
    ]),

    password: new FormControl('', [
      Validators.required,
      Validators.minLength(8)
    ]),

    confirmPassword: new FormControl('', [
      Validators.required
    ])
  });

  constructor(
    private dialog: MatDialog,
    private authService: AuthService
  ) { }

  OnSubmit(): void {
    if (this.registerForm.invalid) {
      this.registerForm.markAllAsTouched();
      return;
    }

    if (this.registerForm.value.password !== this.registerForm.value.confirmPassword) {
      this.dialog.open(CustomAlertComponent, { data: { text: "Password and Confirm Password do not match.", type: ResponseTypeColor.ERROR } });
    }

    const Payload = {
      name: this.registerForm.value.name,
      email: this.registerForm.value.email,
      phone: this.registerForm.value.phone,
      password: this.registerForm.value.password
    };

    this.authService.Register(Payload).subscribe({
      next: (response: ApiResponseDto) => {
        if (response.success === true && response.statusCode === 200) {
          this.dialog.open(CustomAlertComponent, { data: { text: response.message, type: ResponseTypeColor.SUCCESS } });
          this.OnReset();
        } else {
          this.dialog.open(CustomAlertComponent, { data: { text: response.message, type: ResponseTypeColor.ERROR } });
        }
      },
      error: (err: any) => {
        this.dialog.open(CustomAlertComponent, { data: { text: "Failed to register user.", type: ResponseTypeColor.ERROR } });
      }
    });
  }

  OnReset(): void {
    this.registerForm.reset();
  }
}
