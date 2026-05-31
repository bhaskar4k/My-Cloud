import { Component } from '@angular/core';
import {
  FormControl,
  FormGroup,
  Validators,
  ReactiveFormsModule
} from '@angular/forms';

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

  OnSubmit(): void {
    if (this.registerForm.invalid) {
      this.registerForm.markAllAsTouched();
      return;
    }

    console.log(this.registerForm.value);
  }

  OnReset(): void {
    this.registerForm.reset();
  }
}
