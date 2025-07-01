package org.example.backendproject.board.service;

import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.backendproject.board.dto.BoardDTO;
import org.example.backendproject.board.elasticsearch.dto.BoardEsDocument;
import org.example.backendproject.board.elasticsearch.repository.BoardEsRepository;
import org.example.backendproject.board.elasticsearch.service.BoardEsService;
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

import java.io.IOException;
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

    //엘라스틱 서치 서비스
    private final BoardEsService boardEsService;
    private final BoardEsRepository boardEsRepository;

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
        //mysql 저장 완료

        //엘라스틱 서치 저장
        BoardEsDocument doc = BoardEsDocument.builder()
                .id(String.valueOf(board.getId()))
                .title(board.getTitle())
                .content(board.getContent())
                .userId(board.getUser().getId())
                .username(board.getUser().getUserProfile().getUsername())
                .created_date(String.valueOf(board.getCreated_date()))
                .updated_date(String.valueOf(board.getUpdated_date()))
                .build();

        boardEsService.save(doc);



        long end = System.currentTimeMillis();
        log.info("글 작성 완료 , 소요시간"+(end+start)+"ms");

        return toDTO(saved);
    }


    /** 게시글 상세 조회 **/
    @Transactional
    public BoardDTO getBoardDetail(Long boardId) {
        Board board = boardRepository.findById(boardId)
                .orElseThrow(() -> new IllegalArgumentException("게시글 없음: " + boardId));

        //mysql 조회수 증가
        board.setViewCount(board.getViewCount() + 1);
        log.info(String.valueOf(board.getViewCount()));

        //ES 조회수 증가
        BoardEsDocument esDocument = boardEsRepository.findById(String.valueOf(boardId))
                .orElseThrow(() -> new IllegalArgumentException("ES에 게시글 없음 :"+boardId));
        log.info(String.valueOf(esDocument.getView_count()));
        esDocument.setView_count(board.getViewCount());
        log.info(String.valueOf(esDocument.getView_count()));

        boardEsService.save(esDocument);


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
        // mysql 수정 완료

        //엘라스틱 서치 수정
        BoardEsDocument doc = BoardEsDocument.builder()
                .id(String.valueOf(board.getId()))
                .title(board.getTitle())
                .content(board.getContent())
                .userId(board.getUser().getId())
                .username(board.getUser().getUserProfile().getUsername())
                .created_date(String.valueOf(board.getCreated_date()))
                .updated_date(String.valueOf(board.getUpdated_date()))
                .build();

        boardEsService.save(doc);

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

        boardEsRepository.deleteById(String.valueOf(boardId));
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
        dto.setViewCount(board.getViewCount());
        return dto;
    }




    @Transactional
    public void batchSaveBoard(List<BoardDTO> boardDTOList, UserDetails userDetails) {
        Long start = System.currentTimeMillis();
        int batchSize = 1000;

        for (int i = 0; i < boardDTOList.size(); i += batchSize) {
            int end = Math.min(boardDTOList.size(), i + batchSize);
            List<BoardDTO> batchList = boardDTOList.subList(i, end);

            String batchKey = UUID.randomUUID().toString();
            for (BoardDTO dto : batchList) {
                dto.setBatchkey(batchKey);
            }

            try {
                batchRepository.batchInsert(batchList); // SQL 오류 등 발생 가능
            } catch (Exception e) {
                log.error("[BOARD][BATCH] DB 저장 실패: {}", e.getMessage(), e);
                throw new RuntimeException("DB 저장 중 오류 발생", e);  // 반드시 던져야 함
            }

            List<BoardDTO> savedBoards = batchRepository.findByBatchKey(batchKey);

            List<BoardEsDocument> documents = savedBoards.stream()
                    .map(BoardEsDocument::from)
                    .toList();

            try {
                boardEsService.bulkIndexInsert(documents);
            } catch (IOException e) {
                log.error("[BOARD][BATCH] Elasticsearch 인덱싱 실패: {}", e.getMessage(), e);
                throw new RuntimeException("Elasticsearch 인덱싱 중 오류 발생", e);
            }
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