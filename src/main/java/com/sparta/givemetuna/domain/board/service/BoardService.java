package com.sparta.givemetuna.domain.board.service;

import com.sparta.givemetuna.domain.board.dto.BoardListResponseDto;
import com.sparta.givemetuna.domain.board.dto.CreateBoardRequestDto;
import com.sparta.givemetuna.domain.board.dto.CreateBoardResponseDto;
import com.sparta.givemetuna.domain.board.dto.UpdateBoardRequestDto;
import com.sparta.givemetuna.domain.board.entity.Board;
import com.sparta.givemetuna.domain.board.repository.BoardRepository;
import com.sparta.givemetuna.domain.stage.entity.Stage;
import com.sparta.givemetuna.domain.stage.repository.StageRepository;
import com.sparta.givemetuna.domain.user.entity.Role;
import com.sparta.givemetuna.domain.user.entity.User;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.RejectedExecutionException;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class BoardService {

	private final BoardRepository boardRepository;

	private final StageRepository stageRepository;

	// board 생성
	public CreateBoardResponseDto createBoard(CreateBoardRequestDto requestDto, User user) {
		// board 만들기
		Board board = new Board(requestDto);

		// ***board에 user 넣기
        board.setUser(user);

		// board에 user 권한 총책임자 설정
		// board.setBoardRole("총책임자");

		// 3가지 stage 만들어서 넣기
		createDefaultStages(board, user);

		// board 저장
		boardRepository.save(board);


		return new CreateBoardResponseDto(board);
	}

	// user 검증 추가 필요
	public void deleteBoard(Long boardId, User user) {
		//유저 권한 체크
		Board board = checkBoardAuth(boardId, user);

		boardRepository.delete(board);
	}

	// 단일 board 찾기
	@Transactional(readOnly = true)
	public Board getBoard(Long boardId) {
		Board board = boardRepository.findById(boardId)
			.orElseThrow(() -> new IllegalArgumentException("The board does not exist"));
		return board;
	}

	// 모든 board 찾기
	@Transactional(readOnly = true)
	public List<BoardListResponseDto> getAllBoards() {
		List<Board> boardList = boardRepository.findAll();
		return boardList.stream()
			.map(board -> new BoardListResponseDto(board.getName()))
			.collect(Collectors.toList());
	}

	// update
	@Transactional
	public Board updateBoard(Long boardId, UpdateBoardRequestDto requestDto, User user) {
		// user 검증 로직
		Board board = checkBoardAuth(boardId, user);

		board.setName(requestDto.getName());
		return board;
	}

	// Board 작성자 검증
	private Board checkBoardAuth(Long id, User user) {
		// id로 보드를 찾은후
		Board board = getBoardById(id);
		// user의 id와 대조
		if (!user.getId().equals(board.getUser().getId())) {
			throw new RejectedExecutionException("총 책임자만 접근 가능합니다.");
		}
		return board;
	}

	// Board id로 board 찾기
	@Transactional(readOnly = true)
	public Board getBoardById(Long id) {
		return boardRepository.findById(id)
			.orElseThrow(() -> new IllegalArgumentException("The board does not exist"));
	}

	// board 총 책임자 권한 설정
	private void setUserBoardManager(User user) {
	}

	public Board inviteUser(Long boardId, Map<User, Role> users) {
		Board board = getBoard(boardId);
		board.addUsersWithRole(users);
		return board;
	}

	// 기본 Stage 생성 메서드
	@Transactional
	public void createDefaultStages(Board board, User user) {
		List<Stage> defaultStage = new ArrayList<>();

		defaultStage.add(new Stage(board, "백로그", user));
		defaultStage.add(new Stage(board, "프로세스", user));
		defaultStage.add(new Stage(board, "완료", user));
		defaultStage.add(new Stage(board, "비상", user));

		stageRepository.saveAll(defaultStage);
	}
}
