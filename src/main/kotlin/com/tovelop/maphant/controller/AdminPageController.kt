package com.tovelop.maphant.controller

import org.springframework.http.ResponseEntity
import com.tovelop.maphant.configure.security.token.TokenAuthToken
import com.tovelop.maphant.dto.*
import com.tovelop.maphant.mapper.UserMapper
import com.tovelop.maphant.configure.security.PasswordEncoderSHA512

import com.tovelop.maphant.service.AdminPageService
import com.tovelop.maphant.service.BoardService
import com.tovelop.maphant.service.UserService
import com.tovelop.maphant.type.response.Response
import com.tovelop.maphant.type.response.ResponseUnit
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseBody
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@Controller
@RequestMapping("/admin")
class AdminPageController(@Autowired val adminPageService: AdminPageService) {

    @GetMapping("/reportlist/board")
    fun listBoardReport(@RequestParam sortType: String): ResponseEntity<Response<List<AdminBoardReportDTO>>> {
        val findBoardReport = adminPageService.findBoardReport(sortType, 10)
        return if (findBoardReport == null) ResponseEntity.badRequest().body(Response.error("유효하지 않은 정렬 타입입니다."))
            else ResponseEntity.ok().body(Response.success(findBoardReport))
    }
    @GetMapping("/reportinfo/board")
    fun boardReportInfo(@RequestParam boardId: Int): ResponseEntity<Response<List<BoardReportInfoDTO>>> {
        val findBoardReportInfo = adminPageService.findBoardReportInfo(boardId)
        return ResponseEntity.ok().body(Response.success(findBoardReportInfo))
    }
    @GetMapping("/reportlist/comment")
    fun listCommentReport(@RequestParam sortType: String): ResponseEntity<Response<List<AdminCommentReportDTO>>> {
        val findCommentReport = adminPageService.findCommentReport(sortType, 10)
        return if (findCommentReport == null) ResponseEntity.badRequest().body(Response.error("유효하지 않은 정렬 타입입니다."))
        else ResponseEntity.ok().body(Response.success(findCommentReport))
    }
    @GetMapping("/reportinfo/comment")
    fun commentReportInfo(@RequestParam commentId: Int): ResponseEntity<Response<List<CommentReportInfoDTO>>> {
        val findBoardReportInfo = adminPageService.findCommentReportInfo(commentId)
        return ResponseEntity.ok().body(Response.success(findBoardReportInfo))
    }
    @PostMapping("/sanction/board")
    fun sanctionBoard(@RequestParam boardId: Int): ResponseEntity<ResponseUnit> {
        adminPageService.updateBoardSanction(boardId)
        return ResponseEntity.ok(Response.stateOnly(true))
    }
    @PostMapping("/sanction/comment")
    fun sanctionComment(@RequestParam commentId: Int): ResponseEntity<ResponseUnit> {
        adminPageService.updateCommentSanction(commentId)
        return ResponseEntity.ok(Response.stateOnly(true))
    }
    @PostMapping("/sanction/user")
    fun sanctionUser(@RequestBody userReportDTO: UserReportDTO): ResponseEntity<ResponseUnit> {
        //정지할 유저가 작성한 모든 글, 댓글을 임시 블락 상태(3)으로 변경
        adminPageService.updateBoardBlockByUserId(userReportDTO.userId)
        adminPageService.updateCommentBlockByUserId(userReportDTO.userId)
        //유저 제재 내역 테이블에 삽입
        adminPageService.insertUserReport(userReportDTO)
        //유저를 정지 상태(2)로 변경
        adminPageService.updateUserState(userReportDTO.userId, 2)
        return ResponseEntity.ok(Response.stateOnly(true))
    }
    @ResponseBody
    fun listBoardReport() /*: ResponseEntity<Response<List<BoardReportDTO>>>*/ {
        // 검색 후 비어있으면 비어있다고 에러처리
        // 아니면 보드리포트DTO 리스트 반환
    }

    @GetMapping("/login")
    fun loginPage(): String {
        return "admin_login_page"
    }
    @PostMapping("/login")
    fun goToLoginPage(): String {
        return "redirect:/login/"
    }
    @GetMapping("/")
    fun index(): String {
        return "admin_index_page"
    }
}
