package com.hammer.pulsar.controller;

import com.hammer.pulsar.dto.routine.Routine;
import com.hammer.pulsar.dto.routine.RoutineModifyForm;
import com.hammer.pulsar.dto.routine.RoutineRegistForm;
import com.hammer.pulsar.exception.UnauthorizedException;
import com.hammer.pulsar.service.RoutineService;
import com.hammer.pulsar.util.MemoryAuthManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

// 루틴 관련 API 요청을 처리할 REST 컨트롤러
@RestController
@RequestMapping("/routines")
public class RoutineRestController {

    // 루틴 관련 서비스 로직을 처리할 클래스
    private final RoutineService routineService;

    // 컨트롤러의 생성자
    @Autowired
    public RoutineRestController(RoutineService routineService) {
        this.routineService = routineService;
    }

    // 루틴 리스트 요청 API

    /**
     * 루틴 리스트 요청 API <br>
     * 로그인 필요
     *
     * @param memberId
     * @return
     *  200 OK : 성공
     *  401 UNAUTHORIZED : 인증 실패
     */
    @GetMapping("/{memberId}")
    public ResponseEntity<List<Routine>> showAllRoutines(@PathVariable int memberId, HttpServletRequest request) {
        // 인증 정보 확인
        if(MemoryAuthManager.getLoginMember() != memberId) {
            throw new UnauthorizedException();
        }
        // 루틴 정보 가져오기
        List<Routine> allRoutine = routineService.getAllRoutines(memberId);

        return new ResponseEntity<>(allRoutine, HttpStatus.OK);
    }

    // 루틴 상세보기 요청 API
    @GetMapping("/routine/{routineId}")
    public ResponseEntity<Routine> showRoutine(@PathVariable int routineId, HttpServletRequest request) {
        int memberId = MemoryAuthManager.getLoginMember();

        Routine routine = routineService.getRoutineDetail(routineId, memberId);

        return new ResponseEntity<>(routine, HttpStatus.OK);
    }

    // 루틴 작성 API
    @PostMapping("/routine")
    public ResponseEntity<Integer> addNewRoutine(@RequestBody RoutineRegistForm form, HttpServletRequest request) {
        int memberId = MemoryAuthManager.getLoginMember();

        int routineNo = routineService.addNewRoutine(form, memberId);

        // 작성한 루틴의 고유 번호를 응답으로 보낸다.
        return new ResponseEntity<>(routineNo, HttpStatus.CREATED);
    }

    // 루틴 수정 API
    @PutMapping("/routine/{routineId}")
    public ResponseEntity<Void> modifyRoutineInfo(@PathVariable int routineId, @RequestBody RoutineModifyForm form, HttpServletRequest request) {
        int memberId = MemoryAuthManager.getLoginMember();

        form.setRoutineId(routineId);
        routineService.modifyRoutineInfo(form, memberId);

        return new ResponseEntity<>(HttpStatus.OK);
    }

    // 루틴 삭제 API
    @DeleteMapping("/routine/{routineId}")
    public ResponseEntity<Void> removeRoutine(@PathVariable int routineId, HttpServletRequest request) {
        int memberId = MemoryAuthManager.getLoginMember();

        routineService.removeRoutine(routineId, memberId);

        return new ResponseEntity<>(HttpStatus.OK);
    }

}
