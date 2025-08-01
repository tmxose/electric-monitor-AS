package com.eco.service;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.eco.domain.DTO.FileUploadDTO;
import com.eco.domain.DTO.NoticeDTO;
import com.eco.domain.DTO.NoticePageResponseDTO;
import com.eco.exception.ServiceException;
import com.eco.mapper.FileMapper;
import com.eco.mapper.NoticeMapper;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class NoticeServiceImpl implements NoticeService {

	private final NoticeMapper noticeMapper;
	private final FileMapper fileMapper; // T_FILE 테이블 관련 DAO/Mapper

	@Value("${file.upload-dir}")
	private String UPLOAD_DIR; // 실제 서버 경로로 변경 필요

	// 공지사항 목록 조회
	@Override
	public List<NoticeDTO> getNoticeList() {
		try {
			return noticeMapper.selectNoticeList();
		} catch (Exception e) {
			throw new ServiceException("공지사항 리스트 조회 실패", e);
		}
	}

	@Override
	public List<NoticeDTO> getNoticeSearchList(String search_word) {
		try {
			return noticeMapper.selectNoticeSearchList(search_word);
		} catch (Exception e) {
			throw new ServiceException("공지사항 리스트 조회 실패", e);
		}
	}

	// 공지사항 상세 조회
	@Override
	public NoticeDTO getNoticeDetail(int notice_cd) {
		try {
			return noticeMapper.selectDetailNotice(notice_cd);
		} catch (Exception e) {
			throw new ServiceException("공지사항 상세 조회 실패", e);
		}
	}

	// 공지사항 등록
	@Override
	public boolean registerNotice(NoticeDTO notice) {
		try {
			int result = noticeMapper.insertNotice(notice);
			return result > 0;
		} catch (Exception e) {
			throw new ServiceException("공지사항 등록 실패", e);
		}
	}

	// 공지사항 수정
	@Override
	public boolean modifyNotice(NoticeDTO notice) {
		try {
			int result = noticeMapper.updateNotice(notice);
			return result > 0;
		} catch (Exception e) {
			throw new ServiceException("공지사항 등록 실패", e);
		}
	}

	// 공지사항 삭제
	@Override
	public boolean removeNotice(int notice_cd) {
		try {
			int result = noticeMapper.deleteNotice(notice_cd);
			return result > 0;
		} catch (Exception e) {
			throw new ServiceException("공지사항 등록 실패", e);
		}
	}

	// 페이징을 위한 새로운 서비스 메서드 구현
	@Override
	public NoticePageResponseDTO getNoticeListPaged(String searchWord, int page, int size) {
		try {
			int offset = page * size; // 0-based 페이지 번호이므로 page * size

			// 1. 검색 조건에 맞는 전체 공지사항의 총 개수를 조회합니다.
			long totalElements = noticeMapper.countNoticeList(searchWord);

			// 2. 현재 페이지에 해당하는 공지사항 목록을 조회합니다.
			// Note: Mapper의 selectNoticeListPaged는 NoticeVO를 반환하도록 되어 있습니다.
			List<NoticeDTO> content = noticeMapper.selectNoticeListPaged(searchWord, offset, size);

			// NoticePageResponseDTO 객체를 생성하고 모든 페이징 정보를 설정합니다.
			NoticePageResponseDTO response = new NoticePageResponseDTO();
			response.setContent(content);
			response.setTotalElements(totalElements);
			// 전체 페이지 수 계산: (총 항목 수 + 페이지 사이즈 - 1) / 페이지 사이즈 (올림)
			response.setTotalPages((int) Math.ceil((double) totalElements / size));
			response.setCurrentPage(page); // 현재 페이지 번호 (0-based)
			response.setPageSize(size);

			return response;
		} catch (Exception e) {
			throw new ServiceException("페이징된 공지사항 목록 조회 실패", e);
		}
	}

	@Override
	@Transactional // 이 메서드 내의 모든 DB 작업이 하나의 트랜잭션으로 처리됩니다.
	public void registerNoticeWithFiles(NoticeDTO notice, MultipartFile[] files) {
		try {
			 System.out.println("인서트 전: " + notice);

	            int noticeResult = noticeMapper.insertNotice(notice);

	            System.out.println("인서트 후: " + notice);

	            if (noticeResult == 0) {
	                throw new ServiceException("공지사항 등록에 실패했습니다.");
	            }

	            int noticeCd = notice.getNotice_cd();
	            if (noticeCd == 0) {
	                throw new ServiceException("공지사항 코드 획득에 실패했습니다.");
	            }

	            if (files != null && files.length > 0) {
	                String actualUploadPath = UPLOAD_DIR;
	                
	                if (!actualUploadPath.endsWith(File.separator)) {
	                    actualUploadPath += File.separator;
	                }

	                File uploadDirFile = new File(actualUploadPath);
	                System.out.println("파일 저장 경로: "+ uploadDirFile);
	                
	                // 디렉토리 생성 시도 전 로그
	                System.out.println("업로드 디렉토리 존재 여부 확인: "+ uploadDirFile.exists());
	                System.out.println("업로드 디렉토리 생성 시도: "+ actualUploadPath);

	                if (!uploadDirFile.exists()) {
	                    boolean mkdirsSuccess = uploadDirFile.mkdirs(); // 디렉토리 생성 시도
	                    System.out.println("업로드 디렉토리 생성 결과: " + mkdirsSuccess);
	                    if (!mkdirsSuccess) {
	                        throw new ServiceException("업로드 디렉토리 생성에 실패했습니다: " + actualUploadPath);
	                    }
	                }

	                for (MultipartFile file : files) {
	                    if (!file.isEmpty()) {
	                        String originalName = file.getOriginalFilename();
	                        System.out.println("업로드 시도 파일명: " + originalName);
	                        String storedName = UUID.randomUUID().toString() + "_" + originalName;
	                        String filePath = actualUploadPath + storedName;
	                        long fileSize = file.getSize();

	                        Path targetPath = Paths.get(filePath);
	                        System.out.println("실제 파일 저장 경로 구성: "+ targetPath.toAbsolutePath());

	                        // 파일 복사 시도 전 로그
	                        System.out.println("파일 복사 시도: "+originalName+" -> "+ targetPath.toAbsolutePath());
	                        Files.copy(file.getInputStream(), targetPath); // 실제 파일 저장
	                        System.out.println("파일 저장 완료: " + targetPath.toAbsolutePath());
	                        
	                        // FileDTO 생성 및 DB 삽입 시도 전 로그
	                        System.out.println("파일 정보 DB 삽입 시도: "+ originalName);
	                        FileUploadDTO fileDTO = new FileUploadDTO(); 
	                        fileDTO.setNotice_cd(noticeCd);
	                        fileDTO.setOriginal_name(originalName);
	                        fileDTO.setStored_name(storedName);
	                        fileDTO.setFile_path(filePath);
	                        fileDTO.setFile_size(fileSize);

	                        int fileResult = fileMapper.insertFile(fileDTO);
	                        System.out.println("파일 정보 DB 삽입 결과 (행 수): "+ fileResult);

	                        if (fileResult == 0) {
	                            Files.deleteIfExists(targetPath);
	                            throw new ServiceException("파일 정보 DB 저장에 실패했습니다: " + originalName);
	                        }
	                    }
	                }
	            }
		} catch (Exception e) {
			// 예외 발생 시 트랜잭션 롤백
			// @Transactional 어노테이션이 RuntimeException에 대해 기본적으로 롤백을 수행합니다.
			// 따라서 이곳에서는 ServiceException을 throw하여 롤백을 유도합니다.
			System.out.println(e.getMessage());
			throw new ServiceException("공지사항 및 파일 등록 중 오류 발생", e);
		}
	}

	@Override
	public List<FileUploadDTO> getAttachedFiles(int noticeCd) {
		try {
			// FileMapper를 사용하여 첨부 파일 목록을 조회
			return fileMapper.selectFilesByNoticeCd(noticeCd);
		} catch (Exception e) {
			throw new ServiceException("공지사항의 파일 조회 중 오류 발생", e);
		}

	}

	@Override
	public FileUploadDTO getFileDetail(int fileCd) {
		try {
			return fileMapper.selectFileByFileCd(fileCd); // FileMapper에 selectFileByFileCd 메서드가 있다고 가정
		} catch (Exception e) {
			throw new ServiceException("공지사항의 파일 다운로드 중 오류 발생", e);
		}
	}
	
	 // 공지사항 수정 처리 (파일 포함)
    @Override
    @Transactional
    public void modifyNoticeWithFiles(NoticeDTO notice, MultipartFile[] newFiles, String[] deletedFileCds) {
        try {
            // 1. 공지사항 정보 업데이트
            int noticeUpdateResult = noticeMapper.updateNotice(notice);
            if (noticeUpdateResult == 0) {
                throw new ServiceException("공지사항 정보 업데이트에 실패했습니다.");
            }

            // 2. 삭제할 파일 처리 (use_yn을 'N'으로 업데이트)
            if (deletedFileCds != null && deletedFileCds.length > 0) {
                for (String fileCdStr : deletedFileCds) {
                    try {
                        int fileCd = Integer.parseInt(fileCdStr);
                        int deleteResult = fileMapper.updateFileUseYn(fileCd, "N");
                        if (deleteResult == 0) {
                            // 실패해도 트랜잭션 롤백은 하지 않지만, 로그로 경고
                        } else {
                        }
                    } catch (NumberFormatException e) {
                    	
                    }
                }
            }

            // 3. 새로 추가할 파일 처리 (등록 로직과 유사)
            if (newFiles != null && newFiles.length > 0) {
                String actualUploadPath = UPLOAD_DIR;
                if (!actualUploadPath.endsWith(File.separator)) {
                    actualUploadPath += File.separator;
                }

                File uploadDirFile = new File(actualUploadPath);
                if (!uploadDirFile.exists()) {
                    uploadDirFile.mkdirs();
                }

                for (MultipartFile file : newFiles) {
                    if (!file.isEmpty()) {
                        String originalName = file.getOriginalFilename();
                        String storedName = UUID.randomUUID().toString() + "_" + originalName;
                        String filePath = actualUploadPath + storedName;

                        long fileSize = file.getSize();

                        Path targetPath = Paths.get(filePath);
                        Files.copy(file.getInputStream(), targetPath);

                        FileUploadDTO fileDTO = new FileUploadDTO();
                        fileDTO.setNotice_cd(notice.getNotice_cd()); 
                        fileDTO.setOriginal_name(originalName);
                        fileDTO.setStored_name(storedName);
                        fileDTO.setFile_path(filePath);
                        fileDTO.setFile_size(fileSize);

                        int fileInsertResult = fileMapper.insertFile(fileDTO);
                        if (fileInsertResult == 0) {
                            Files.deleteIfExists(targetPath);
                            throw new ServiceException("새 파일 정보 DB 저장에 실패했습니다: " + originalName);
                        } else {

                        }
                    }
                }
            }
        } catch (Exception e) {
            throw new ServiceException("공지사항 및 파일 수정 중 오류 발생", e);
        }
    }

}
