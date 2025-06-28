package org.example.backendproject.board.service;

import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.backendproject.board.dto.BoardDTO;
import org.example.backendproject.board.entity.Board;
import org.example.backendproject.board.repository.BatchRepository;
import org.example.backendproject.board.repository.BoardRepository;
import org.example.backendproject.user.entity.User;
import org.example.backendproject.user.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class BoardService {

    private final BoardRepository boardRepository;
    private final UserRepository userRepository;

    private final BatchRepository batchRepository;

    private final EntityManager em;

    /** 글 등록 **/
    @Transactional
    public BoardDTO createBoard(BoardDTO boardDTO) {

        long start = System.currentTimeMillis();
        log.info("글작성 메서드 시작");

        // userId(PK)를 이용해서 User 조회
        if (boardDTO.getUser_id() == null)
            throw new IllegalArgumentException("userId(PK)가 필요합니다!");

        // 연관관계 매핑!
        // 작성자 User 엔티티 조회 (userId 필요)
        User user = userRepository.findById(boardDTO.getUser_id())
                .orElseThrow(() -> new IllegalArgumentException("작성자 정보가 올바르지 않습니다."));

        /** mysql 저장 **/
        Board board = new Board();
        board.setTitle(boardDTO.getTitle());
        board.setContent(boardDTO.getContent());
        // 연관관계 매핑!
        board.setUser(user);

        log.info(String.valueOf(boardDTO.getCreated_date()));
        board.setCreated_date(boardDTO.getCreated_date());
        log.info(String.valueOf(board.getCreated_date()));
        board.setUpdated_date(boardDTO.getUpdated_date());

        Board saved = boardRepository.save(board);

        long end = System.currentTimeMillis();
        log.info("글 작성 완료 , 소요시간"+(end+start)+"ms");

        return toDTO(saved);
    }


    /** 게시글 상세 조회 **/
    @Transactional(readOnly = true)
    public BoardDTO getBoardDetail(Long boardId) {
        Board board = boardRepository.findById(boardId)
                .orElseThrow(() -> new IllegalArgumentException("게시글 없음: " + boardId));
        return toDTO(board);
    }

    /** 게시글 수정 **/
    @Transactional
    public BoardDTO updateBoard(Long boardId, BoardDTO dto, UserDetails userDetails) {
        long start = System.currentTimeMillis();
        log.info("글 수정 메서드 시작");

        Board board = boardRepository.findById(boardId)
                .orElseThrow(() -> new IllegalArgumentException("게시글 없음: " + boardId));
        if (board.getUser().getId().equals(userDetails.getUsername())){
            throw new IllegalArgumentException("해당 게시글에 대한 수정 권한이 없습니다.");
        }
        board.setTitle(dto.getTitle());
        board.setContent(dto.getContent());
        boardRepository.save(board);
        long end = System.currentTimeMillis();
        log.info("글 수정 완료 , 소요시간"+(end+start)+"ms");
        return toDTO(board);
    }


    /** 게시글 삭제 **/
    @Transactional
    public void deleteBoard(Long boardId, UserDetails userDetails) {
        Board board = boardRepository.findById(boardId)
                .orElseThrow(() -> new IllegalArgumentException("게시글 없음: " + boardId));
        if (board.getUser().getId().equals(userDetails.getUsername())){
            throw new IllegalArgumentException("해당 게시글에 대한 수정 권한이 없습니다.");
        }
        boardRepository.deleteById(boardId);
    }


    /** 페이징 적용 전 **/
    /** 페이징 적용 전 **/
    /** 페이징 적용 전 **/
    // 게시글 전체 목록
    @Transactional(readOnly = true)
    public List<BoardDTO> getBoardList() {
        return boardRepository.findAll().stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }
    // 게시글 검색  페이징 아님
    public List<BoardDTO> searchBoards(String keyword) {
        return boardRepository.searchKeyword(keyword);
    }




    /** 페이징 적용 후 **/
    /** 페이징 적용 후 **/
    /** 페이징 적용 후 **/
    //페이징 전체 목록
    public Page<BoardDTO> getBoards(int page, int size) {
        return boardRepository.findAllPaging(PageRequest.of(page, size)); //페이저블에 페이징에대한 정보를 담아서 레포지토리에 전달하는 역할
        //    return boardRepository.findAllWithDto(PageRequest.of(page, size, Sort.by("id").ascending())); //함수로 정렬
    }
    //페이징 검색 목록
    public Page<BoardDTO> searchBoardsPage(String keyword, int page, int size) {
        return boardRepository.searchKeywordPaging(keyword, PageRequest.of(page, size));
    }


    // Entity → DTO 변환
    private BoardDTO toDTO(Board board) {
        BoardDTO dto = new BoardDTO();
        dto.setId(board.getId());
        dto.setTitle(board.getTitle());
        dto.setContent(board.getContent());

        dto.setUser_id(board.getUser().getId());
        dto.setUsername(board.getUser() != null ? board.getUser().getUserProfile().getUsername() : null); // ★ username!

        dto.setCreated_date(board.getCreated_date());
        dto.setUpdated_date(board.getUpdated_date());
        return dto;
    }




    /** 배치작업 **/
    @Transactional
    public void batchSaveBoard(List<BoardDTO> boardDTOList,UserDetails userDetails) {
        Long start = System.currentTimeMillis();

        int batchsize = 1000; //한번에 처리할 배치 크기
        for (int i = 0; i < boardDTOList.size(); i+=batchsize) { //i는 1000씩 증가
            //전체 데이터를 1000개씩 잘라서 배치리스트에 담습니다.

            int end = Math.min(boardDTOList.size(), i+batchsize); //두개의 숫자중에 작은 숫자를 반황ㄴ
            List<BoardDTO> batchList = boardDTOList.subList(i, end);

            //전체 데이터에서 1000씩 작업을 하는데 마지막 데이터가 1000개가 안될수도있으니
            //Math.min()으로 전체 크기를 넘지 않게 마지막 인덱스를 계산해서 작업합니다.


            //내가 넣은 데이터만 엘라스틱서치에 동기화하기 위해 uuid 생성
            String batchKey = UUID.randomUUID().toString();
            for (BoardDTO dto : batchList) {
                dto.setBatchkey(batchKey);
            }


            // 1. MySQL로 INSERT
            batchRepository.batchInsert(batchList);

        }




        Long end = System.currentTimeMillis();
        log.info("[BOARD][BATCH] 전체 저장 소요 시간(ms): {}", (end - start));
    }

    @Transactional
    public void boardSaveAll(List<Board> boardList) {
        long start = System.currentTimeMillis();

        for (int i = 0; i < boardList.size(); i++) {
            em.persist(boardList.get(i));
            if(i%1000 == 0){
                em.flush();
                em.clear();
            }
        }
        long end = System.currentTimeMillis();
        log.info("JPA board saveAll 저장 소요 시간 (ms)" + (end - start));
    }



}