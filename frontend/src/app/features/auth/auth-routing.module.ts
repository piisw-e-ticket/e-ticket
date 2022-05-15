import { HttpClientModule } from '@angular/common/http';
import { NgModule } from '@angular/core';
import { ReactiveFormsModule } from '@angular/forms';
import { RouterModule, Routes } from '@angular/router';
import { AuthComponent } from './auth.component';
import { LoginComponent } from './components/login/login.component';
import { ProfileComponent } from './components/profile/profile.component';
import { RegisterComponent } from './components/register/register.component';
import { AuthenticatedGuard } from './guards/authenticated.guard';
import { UserResolver } from './resolvers/user.resolver';

const routes: Routes = [
  {
    path: "aut",
    pathMatch: "full",
    redirectTo: "/aut/login"
  },
  {
    path: "aut",
    component: AuthComponent,
    children: [
      {
        path: "login",
        component: LoginComponent
      },
      {
        path: "register",
        component: RegisterComponent
      },
      {
        path: "profile",
        component: ProfileComponent,
        canActivate: [
          AuthenticatedGuard
        ]
      }
    ]
  }
];

@NgModule({
  imports: [
    RouterModule.forChild(routes),
    ReactiveFormsModule,
    HttpClientModule
  ],
  exports: [RouterModule]
})
export class AuthRoutingModule { }
