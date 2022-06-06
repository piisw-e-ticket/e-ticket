export interface JwtDto{
    exp: number,
    fid: string,
    iat: number,
    role: string,
    sub: string
}