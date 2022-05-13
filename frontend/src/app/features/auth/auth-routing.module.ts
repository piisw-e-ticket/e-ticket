import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { AuthComponent } from './auth.component';
import { LoginComponent } from './components/login/login.component';
import { ProfileComponent } from './components/profile/profile.component';
import { AuthenticatedGuard } from './guards/authenticated.guard';
import { UserResolver } from './resolvers/user.resolver';

const routes: Routes = [
  {
    path: "auth",
    pathMatch: "full",
    redirectTo: "/auth/login"
  },
  {
    path: "auth",
    component: AuthComponent,
    children: [
      {
        path: "login",
        component: LoginComponent
      },
      {
        path: "profile",
        component: ProfileComponent,
        resolve: {
          user: UserResolver
        },
        canActivate: [
          AuthenticatedGuard
        ]
      }
    ]
  }
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class AuthRoutingModule { }
