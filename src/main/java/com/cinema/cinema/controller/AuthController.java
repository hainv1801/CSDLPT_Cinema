package com.cinema.cinema.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.cinema.cinema.dto.request.ReqLoginDTO;
import com.cinema.cinema.dto.response.ResLoginDTO;
import com.cinema.cinema.entity.NguoiDung;
import com.cinema.cinema.exception.IdInvalidException;
import com.cinema.cinema.service.NguoiDungService;
import com.cinema.cinema.util.ApiMessage;
import com.cinema.cinema.util.SecurityUtil;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1")
public class AuthController {

    private final NguoiDungService nguoiDungService;
    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    private final SecurityUtil securityUtil;

    @Value("${jwt.refresh-token-validity-in-seconds}")
    private long refreshTokenExpiration;

    public AuthController(NguoiDungService nguoiDungService,
            AuthenticationManagerBuilder authenticationManagerBuilder,
            SecurityUtil securityUtil) {
        this.nguoiDungService = nguoiDungService;
        this.authenticationManagerBuilder = authenticationManagerBuilder;
        this.securityUtil = securityUtil;
    }

    @PostMapping("/auth/register")
    @ApiMessage("Register")
    public ResponseEntity<NguoiDung> register(@Valid @RequestBody NguoiDung postManUser) throws IdInvalidException {

        // 1. Kiểm tra tài khoản đã tồn tại trong Database chưa
        boolean isTaiKhoanExist = this.nguoiDungService.isTaiKhoanExist(postManUser.getTaiKhoan());
        boolean isEmailExist = this.nguoiDungService.isEmailExist(postManUser.getEmail());
        if (isTaiKhoanExist) {
            // Ném lỗi nếu tài khoản đã có người đăng ký
            throw new IdInvalidException(
                    "Tài khoản '" + postManUser.getTaiKhoan() + "' đã tồn tại, vui lòng chọn tên khác.");
        }
        if (isEmailExist) {
            // Ném lỗi nếu email đã có người đăng ký
            throw new IdInvalidException(
                    "Email '" + postManUser.getEmail() + "' đã tồn tại, vui lòng chọn email khác.");
        }
        // 2. Gọi Service để mã hóa mật khẩu và lưu xuống Database
        NguoiDung newUser = this.nguoiDungService.handleCreateUser(postManUser);

        // 3. Trả về HTTP Status 201 (Created) và thông tin User vừa tạo
        return ResponseEntity.status(HttpStatus.CREATED).body(newUser);
    }

    @PostMapping("/auth/login")
    @ApiMessage("Login")
    public ResponseEntity<ResLoginDTO> login(@Valid @RequestBody ReqLoginDTO loginDto) {
        // 1. Đóng gói tài khoản & mật khẩu
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                loginDto.getTaiKhoan(), loginDto.getMatKhau());

        // 2. Spring Security tự động mã hóa mật khẩu và check với Database
        Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);

        // 3. Set thông tin vào Context
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // 4. Lấy thông tin user từ DB để gen Token
        ResLoginDTO res = new ResLoginDTO();
        NguoiDung currentUserDB = this.nguoiDungService.handleGetUserByUsername(loginDto.getTaiKhoan());

        if (currentUserDB != null) {
            ResLoginDTO.UserLogin userLogin = new ResLoginDTO.UserLogin(
                    currentUserDB.getIdNguoiDung(),
                    currentUserDB.getTaiKhoan(),
                    currentUserDB.getHoTen(),
                    currentUserDB.getVaiTro(),
                    currentUserDB.getMaCoSo());
            res.setUser(userLogin);
        }

        // 5. Gen Token
        String access_token = this.securityUtil.createAccessToken(authentication.getName(), res);
        res.setAccessToken(access_token);
        String refresh_token = this.securityUtil.createRefreshToken(loginDto.getTaiKhoan(), res);

        // 6. Cập nhật Refresh Token xuống Database
        this.nguoiDungService.updateUserToken(refresh_token, loginDto.getTaiKhoan());

        // 7. Gắn Refresh Token vào Cookie
        ResponseCookie resCookies = ResponseCookie
                .from("refresh_token", refresh_token)
                .httpOnly(true)
                .secure(false) // Đang test ở localhost HTTP nên để false
                .path("/")
                .maxAge(refreshTokenExpiration)
                .build();

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, resCookies.toString())
                .body(res);
    }

    @GetMapping("/auth/account")
    @ApiMessage("fetch account")
    public ResponseEntity<ResLoginDTO.UserGetAccount> getAccount() {
        String taiKhoan = SecurityUtil.getCurrentUserLogin().orElse("");
        NguoiDung currentUserDB = this.nguoiDungService.handleGetUserByUsername(taiKhoan);

        ResLoginDTO.UserLogin userLogin = new ResLoginDTO.UserLogin();
        ResLoginDTO.UserGetAccount userGetAccount = new ResLoginDTO.UserGetAccount();

        if (currentUserDB != null) {
            userLogin.setId(currentUserDB.getIdNguoiDung());
            userLogin.setTaiKhoan(currentUserDB.getTaiKhoan());
            userLogin.setHoTen(currentUserDB.getHoTen());
            userLogin.setVaiTro(currentUserDB.getVaiTro());
            userLogin.setMaCoSo(currentUserDB.getMaCoSo());
            userGetAccount.setUser(userLogin);
        }
        return ResponseEntity.ok().body(userGetAccount);
    }

    @GetMapping("/auth/refresh")
    @ApiMessage("Get User by refresh token")
    public ResponseEntity<ResLoginDTO> getRefreshToken(
            @CookieValue(name = "refresh_token") String refresh_token) throws IdInvalidException {

        // 1. Kiểm tra token hợp lệ
        Jwt decodedToken = this.securityUtil.checkValidRefreshToken(refresh_token);
        String taiKhoan = decodedToken.getSubject();

        // 2. Kiểm tra user và token trong DB
        NguoiDung currentUser = this.nguoiDungService.getUserByRefreshTokenAndTaiKhoan(refresh_token, taiKhoan);
        if (currentUser == null) {
            throw new IdInvalidException("Refresh Token không hợp lệ hoặc đã hết hạn");
        }

        // 3. Cấp Access Token mới
        ResLoginDTO res = new ResLoginDTO();
        ResLoginDTO.UserLogin userLogin = new ResLoginDTO.UserLogin(
                currentUser.getIdNguoiDung(), currentUser.getTaiKhoan(), currentUser.getHoTen(),
                currentUser.getVaiTro(), currentUser.getMaCoSo());
        res.setUser(userLogin);

        String access_token = this.securityUtil.createAccessToken(taiKhoan, res);
        res.setAccessToken(access_token);

        return ResponseEntity.ok().body(res);
    }

    @PostMapping("/auth/logout")
    @ApiMessage("Logout User")
    public ResponseEntity<Void> logout() throws IdInvalidException {
        String taiKhoan = SecurityUtil.getCurrentUserLogin().orElse("");
        if (taiKhoan.isEmpty()) {
            throw new IdInvalidException("Access Token không hợp lệ");
        }

        // Xóa token trong DB và xóa Cookie
        this.nguoiDungService.updateUserToken(null, taiKhoan);
        ResponseCookie deleteCookie = ResponseCookie.from("refresh_token", null)
                .httpOnly(true).secure(false).path("/").maxAge(0).build();

        return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE, deleteCookie.toString()).build();
    }
}