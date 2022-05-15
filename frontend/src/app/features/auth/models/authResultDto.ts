export interface AuthResultDto {
    accessToken: string,
    accessTokenExpirationDate: string,
    refreshToken: string,
    refreshTokenExpirationDate: string,
    roles: string[],
    type: string
}