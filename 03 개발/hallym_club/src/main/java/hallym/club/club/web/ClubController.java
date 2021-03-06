package hallym.club.club.web;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.servlet.ModelAndView;

import hallym.club.club.service.ClubService;
import hallym.club.club.vo.ClubVO;
import hallym.club.clubmember.service.ClubMemberService;
import hallym.club.clubmember.vo.ClubMemberVO;
import hallym.club.exception.ClubExistException;
import hallym.club.exception.UserNotExistException;
import hallym.club.user.service.UserService;
import hallym.club.user.vo.UserVO;
import hallym.club.utils.CommonUtils;

@Controller
public class ClubController {
	
	@Resource(name = "clubService")
	private ClubService clubService;
	
	@Resource(name = "userService")
	private UserService userService;
	
	@Resource(name = "clubMemberService")
	private ClubMemberService clubMemberService;
	
	
	@RequestMapping(value="/clubIntro.do")
	public ModelAndView clubIntro(HttpServletRequest request, HttpServletResponse response,
							 ModelAndView mav,
							 @RequestParam(value = "club_id", required = false, defaultValue ="") String club_id) {
		HttpSession session = request.getSession();
		UserVO userVO = (UserVO) session.getAttribute("userVO");
		
		if(userVO == null) {
			CommonUtils.showAlert(response, "로그인이 필요한 서비스입니다.", "login.do");
			return null;
		}
		session.setAttribute("club_id", club_id);
		Map<String, Object> clubParams = new HashMap<String, Object>();
		clubParams.put("club_id", club_id);
		ClubVO clubVO = clubService.getClub(clubParams);
		
		Map<String, Object> presidentParams = new HashMap<String, Object>();
		presidentParams.put("club_id", club_id);
		clubVO.setPresident(clubMemberService.getClubPresident(presidentParams).getName());
		
		Map<String, Object> memberParams = new HashMap<String, Object>();
		memberParams.put("club_id", club_id);
		memberParams.put("id", userVO.getId());
		String staff_cd = clubMemberService.getStaffCD(memberParams);

		boolean isStaff = false;
		if(staff_cd.equals("004001") || staff_cd.equals("004002"))
			isStaff = true;
		System.err.println("[clubIntro.do] Intro_save_file: " + clubVO.getIntro_save_file_nm());
		System.err.println("[clubIntro.do] poster_save_file: " + clubVO.getPoster_save_file_nm());
		System.err.println("[clubIntro.do] staff_cd: " + staff_cd);
		System.err.println("[clubIntro.do] isStaff: " + isStaff);
		
		/* clubPlatform */
		mav.addObject("club_id", club_id);
		mav.addObject("club_name", clubVO.getClub_nm());
		mav.addObject("open_dt", clubVO.getOpen_dt());
		mav.addObject("president_nm", clubVO.getPresident());
		mav.addObject("isStaff", isStaff);
		mav.addObject("club_intro", clubVO.getIntro_save_file_nm());
		mav.addObject("club_poster", clubVO.getPoster_save_file_nm());
		
		mav.setViewName("club/clubIntro");
		return mav;
	}
	
	
	@RequestMapping(value="/clubMemberList.do")
	public ModelAndView clubMemberList(HttpServletRequest request, HttpServletResponse response,
							 ModelAndView mav,
							 @RequestParam(value = "club_id", required = false, defaultValue ="") String club_id) {
		HttpSession session = request.getSession();
		UserVO userVO = (UserVO) session.getAttribute("userVO");
		
		if(userVO == null) {
			CommonUtils.showAlert(response, "로그인이 필요한 서비스입니다.", "login.do");
			return null;
		}
		session.setAttribute("club_id", club_id);
		Map<String, Object> clubParams = new HashMap<String, Object>();
		clubParams.put("club_id", club_id);
		ClubVO clubVO = clubService.getClub(clubParams);
		
		Map<String, Object> presidentParams = new HashMap<String, Object>();
		presidentParams.put("club_id", club_id);
		clubVO.setPresident(clubMemberService.getClubPresident(presidentParams).getName());
		
		Map<String, Object> memberParams = new HashMap<String, Object>();
		memberParams.put("club_id", club_id);
		memberParams.put("id", userVO.getId());
		String staff_cd = clubMemberService.getStaffCD(memberParams);

		boolean isStaff = false;
		if(staff_cd.equals("004001") || staff_cd.equals("004002"))
			isStaff = true;
		System.err.println("[clubIntro.do] Intro_save_file: " + clubVO.getIntro_save_file_nm());
		System.err.println("[clubIntro.do] poster_save_file: " + clubVO.getPoster_save_file_nm());
		System.err.println("[clubIntro.do] staff_cd: " + staff_cd);
		System.err.println("[clubIntro.do] isStaff: " + isStaff);
		
		Map<String, Object> membersParams = new HashMap<String, Object>();
		membersParams.put("club_id", club_id);
		membersParams.put("join_cd", "008001");
		List<ClubMemberVO> memberList = null;
		memberList = clubMemberService.getAllClubMember(membersParams);
		
		/* clubPlatform */
		mav.addObject("club_id", club_id);
		mav.addObject("club_name", clubVO.getClub_nm());
		mav.addObject("open_dt", clubVO.getOpen_dt());
		mav.addObject("president_nm", clubVO.getPresident());
		mav.addObject("isStaff", isStaff);
		mav.addObject("club_intro", clubVO.getIntro_save_file_nm());
		mav.addObject("club_poster", clubVO.getPoster_save_file_nm());

		/*  clubMemberList */
		mav.addObject("memberList", memberList);
		mav.setViewName("club/clubMemberList");
		return mav;
	}
	
	@RequestMapping(value="/clubSearch.do")
	public ModelAndView clubSearch(HttpServletRequest request, HttpServletResponse response,
							 ModelAndView mav,
							 @RequestParam(value = "gb_cd", required = false, defaultValue ="") String gb_cd,
							 @RequestParam(value = "at_cd", required = false, defaultValue ="") String at_cd,
							 @RequestParam(value = "search", required = false, defaultValue ="") String search,
							 @RequestParam(value = "page", required = false, defaultValue = "1") String pageNumber) {
		HttpSession session = request.getSession();
		
		if(!search.isEmpty()) {
			search = CommonUtils.getUTF8(search);
		}
		if(at_cd.equals("") || at_cd.equals("002"))
			session.setAttribute("at_cd", "002");
		else 
			session.setAttribute("at_cd", at_cd);
		
		List<ClubVO> clubList = null;
		
		int clubListCount = 1;
		int limit = 5;
		int currPage = Integer.parseInt(pageNumber);
		currPage = (currPage < 1)?1:currPage;
		int prevPage = 1;
		int nextPage = 1;
		int totalPage = 1;
		int startNum = 1; // 범위 시작
		int endNum = 1; // 범위 끝	
		
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("gb_cd", gb_cd);
		params.put("at_cd", at_cd);
		params.put("search", search);
		params.put("limit", limit);
		System.err.println("[clubSearch.do] gb_cd: " + gb_cd);
		System.err.println("[clubSearch.do] at_cd: " + at_cd);
		System.err.println("[clubSearch.do] search: " + search);
		
		try{ clubListCount = clubService.getClubListCnt(params); }
		catch (Exception e) { clubListCount=0; }
		try { totalPage = clubService.getTotalPageCnt(params); }
		catch (Exception e) { totalPage=0; }
		
		/* 페이지 번호에 따른 가져올 게시글 범위 */
		if(totalPage <= 1) {
			startNum = 1;
			endNum = (clubListCount < limit) ? clubListCount:limit;
			currPage = 1;
			prevPage = currPage;
			nextPage = currPage;
		} else {
			startNum = (currPage * limit) - limit + 1;
			endNum = currPage * limit;
			prevPage = currPage - 1;
			prevPage = (prevPage < 1) ? 1: prevPage;
			nextPage = (clubListCount > endNum) ? (currPage+1):currPage;
		}
		
		params = new HashMap<String, Object>();
		params.put("startNum", startNum);
		params.put("endNum", endNum);			
		params.put("gb_cd", gb_cd);
		params.put("at_cd", at_cd);
		params.put("search", search);	
		
		clubList = clubService.getClubList(params);			
		System.err.println("[clubSearch.do] clubList: \n" + clubList);
		System.err.println("[clubSearch.do] clubListCount: " + clubListCount);
		System.err.println("[clubSearch.do] totalPage: " + totalPage);
		
		for(ClubVO club : clubList) {
			Map<String, Object> cntParams = new HashMap<String, Object>();
			cntParams.put("club_id", club.getClub_id());
			cntParams.put("opt", 1);
			cntParams.put("join_cd", "008001");
			club.setCnt(clubMemberService.getClubMemberCnt(cntParams));
			
			Map<String, Object> presidentParams = new HashMap<String, Object>();
			presidentParams.put("club_id", club.getClub_id());
			club.setPresident(clubMemberService.getClubPresident(presidentParams).getName());
		}
		
		mav.addObject("at_cd", at_cd);
		mav.addObject("gb_cd", gb_cd);
		mav.addObject("search", search);
		mav.addObject("totalPage", totalPage);
		mav.addObject("prevPage", prevPage);
		mav.addObject("currPage", currPage);
		mav.addObject("nextPage", nextPage);
		mav.addObject("clubList", clubList);
		mav.addObject("clubListCount", clubListCount);
		mav.setViewName("hallym/clubSearch");
		return mav;
	}
	
	@RequestMapping(value="/topClub.do")
	public ModelAndView getTopClub(HttpServletRequest request, HttpServletResponse response,
							 ModelAndView mav,
							 @RequestParam(value = "gb_cd", required = false, defaultValue ="") String gb_cd,
							 @RequestParam(value = "at_cd", required = false, defaultValue ="") String at_cd) {
		HttpSession session = request.getSession();
		if(at_cd.equals("") || at_cd.equals("002"))
			session.setAttribute("at_cd", "002001");
		else 
			session.setAttribute("at_cd", at_cd);
		List<ClubVO> clubTopList = null;
		
		int opt  = 1;
		if(at_cd.equals("002002")) 
			opt = 2;
		
		if( gb_cd.equals(""))
			gb_cd="001001";
	
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("gb_cd", gb_cd);
		params.put("opt", opt);
		
		clubTopList = clubService.getTopClub(params);
		
		for(ClubVO club : clubTopList) {
			Map<String, Object> cntParams = new HashMap<String, Object>();
			cntParams.put("club_id", club.getClub_id());
			cntParams.put("opt", 1);
			cntParams.put("join_cd", "008001");
			club.setCnt(clubMemberService.getClubMemberCnt(cntParams));
			
			Map<String, Object> presidentParams = new HashMap<String, Object>();
			presidentParams.put("club_id", club.getClub_id());
			club.setPresident(clubMemberService.getClubPresident(presidentParams).getName());
		}
		
		mav.addObject("clubList", clubTopList);
		mav.setViewName("hallym/topClub");
		
		return mav;
	}
	@RequestMapping(value="/createClub.do",  method = RequestMethod.GET)
	public ModelAndView createClubView(HttpServletRequest request, HttpServletResponse response,
							 ModelAndView mav) {
		HttpSession session = request.getSession();
		UserVO userVO = (UserVO) session.getAttribute("userVO");
		
		if(userVO == null) {
			CommonUtils.showAlert(response, "로그인이 필요한 서비스입니다.", "login.do");
			return null;
		}
		else {
			mav.addObject("user_id",userVO.getId());
			mav.setViewName("hallym/createClub");

			return mav;
		}	
	}
	
	@RequestMapping(value="/createClub.do",  method = RequestMethod.POST)
	public ModelAndView createClub(HttpServletRequest request, HttpServletResponse response,
							 MultipartHttpServletRequest multi,
							 ModelAndView mav,
							 @RequestParam(value = "club_nm", required = false, defaultValue ="") String club_nm,
							 @RequestParam(value = "club_gb_cd", required = false, defaultValue ="") String club_gb_cd,
							 @RequestParam(value = "club_at_cd", required = false, defaultValue ="") String club_at_cd,
							 @RequestParam(value = "club_aim", required = false, defaultValue ="") String club_aim,
							 @RequestParam(value = "club_active", required = false, defaultValue ="") String club_active,
							 @RequestParam(value = "club_room", required = false, defaultValue ="") String club_room,
							 @RequestParam(value = "open_dt", required = false, defaultValue ="") String open_dt,
							 @RequestParam(value = "user_id", required = false, defaultValue ="") String user_id) {
	
		club_nm = CommonUtils.getUTF8(club_nm);
		club_aim = CommonUtils.getUTF8(club_aim);
		club_active = CommonUtils.getUTF8(club_active);
		club_room = CommonUtils.getUTF8(club_room);
		
		Map<String, Object> nameCheckParams = new HashMap<String, Object>();
		nameCheckParams.put("club_nm", club_nm);

		/* 클럽 이름이 이미 존재 하는지  체크 */
		if(club_nm.equals(clubService.getClubName(nameCheckParams))) {
			try {
				throw new ClubExistException("ClubExistException: 이미" + club_nm + " 은(는) 존재 합니다.");
			} catch (ClubExistException e) {
				System.err.println("[createClub.do] ClubExistException: " + e.getMessage());
				CommonUtils.showAlert(response, "이미 존재하는 동아리 입니다. [생성 오류]", "index.do");
				return null;
			}
		}
		
		Map<String, Object> userParams = new HashMap<String, Object>();
		userParams.put("ID", user_id);	
		UserVO userVO = userService.getUserVO(userParams);
		
		/* 유저 정보 확인 */
		if(userVO.getId() == null) {
			try {
				throw new UserNotExistException("UserNotExistException: " + user_id + "라는 user가 존재 하지 않습니다.");
			} catch (UserNotExistException e) {
				System.err.println("[createClub.do] UserNotExistException: " + e.getMessage());
				CommonUtils.showAlert(response, "유저 DB 오류 ", "index.do");
				return null;
			}
		}
		
		ClubVO clubVO = new ClubVO();
		// https://cofs.tistory.com/40 참고
		// https://powerku.tistory.com/12 참고
		// https://m.blog.naver.com/PostView.nhn?blogId=jxs2&logNo=110177957010&proxyReferer=https:%2F%2Fwww.google.com%2F 참고
		String directory = multi.getSession().getServletContext().getRealPath("/upload/club/");
		System.err.println("[createClub.do] directory: " + directory);
		File upDir = new File(directory);
		if (!upDir.exists()) {
			upDir.mkdirs();
		}
		
		Iterator<String> fileNames = multi.getFileNames();
		String parameter = (String) fileNames.next();
		MultipartFile mFile = multi.getFile(parameter);
		System.err.println("[createClub.do] parmeter: " + parameter);
		String fileName = mFile.getOriginalFilename().toLowerCase();

		System.err.println("[createClub.do] fileName: " + fileName);
		String fileSaveName = CommonUtils.uploadFile(fileName).toLowerCase();
		
		/* Intro */
		if (fileName == null || fileName.isEmpty()) {
			clubVO.setIntro_file_nm(fileName);
			clubVO.setIntro_save_file_nm(fileSaveName);
			
		} else {
			if (!fileName.toLowerCase().endsWith(".jpg") && !fileName.toLowerCase().endsWith(".png") && !fileName.toLowerCase().endsWith(".gif")
					&& !fileName.endsWith(".bmp")) {
				File file = new File(directory + fileSaveName);
				file.delete();
				CommonUtils.showAlert(response, "업로드할 수 없는 확장자입니다.", "createClub.do");
				
			} else {
				try {
					mFile.transferTo(new File(directory + fileSaveName));
				} catch (IllegalStateException | IOException e) {
					System.err.println("[createClub.do] transferTo Error: " + e.getMessage());
					CommonUtils.showAlert(response, "클럽 생성 오류", "index.do");
					return null;
				}
				clubVO.setIntro_file_nm(fileName);
				clubVO.setIntro_save_file_nm(fileSaveName);
			}
		}

		parameter = (String) fileNames.next();
		mFile = multi.getFile(parameter);
		System.err.println("[createClub.do] parmeter2: " + parameter);
		fileName = mFile.getOriginalFilename().toLowerCase();
		System.err.println("[createClub.do] fileName2: " + fileName);
		fileSaveName = CommonUtils.uploadFile(fileName).toLowerCase();

		/* Poster */
		if (fileName == null || fileName.isEmpty()) {
			
			clubVO.setPoster_file_nm(fileName);
			clubVO.setPoster_save_file_nm(fileSaveName);
		} else {
			if (!fileName.toLowerCase().endsWith(".jpg") && !fileName.toLowerCase().endsWith(".png") && !fileName.toLowerCase().endsWith(".gif")
					&& !fileName.endsWith(".bmp")) {
				File file = new File(directory + fileSaveName);
				file.delete();
				CommonUtils.showAlert(response, "업로드할 수 없는 확장자입니다.", "createClub.do");
			} else {
				try {
					mFile.transferTo(new File(directory + fileSaveName));
				} catch (IllegalStateException | IOException e) {
					System.err.println("[createClub.do] transferTo Error: " + e.getMessage());
					CommonUtils.showAlert(response, "클럽 생성 오류", "index.do");
					return null;
				}
				clubVO.setPoster_file_nm(fileName);
				clubVO.setPoster_save_file_nm(fileSaveName);
			}
		}		
		
		Map<String, Object> clubParams = new HashMap<String, Object>();
		clubParams.put("club_nm", club_nm);
		clubParams.put("club_gb_cd", club_gb_cd);
		clubParams.put("club_at_cd", club_at_cd);
		clubParams.put("club_cnt", 1);
		clubParams.put("club_aim", club_aim);
		clubParams.put("club_active", club_active);
		clubParams.put("club_room", club_room);
		clubParams.put("open_dt", open_dt);
		clubParams.put("intro_file_nm", clubVO.getIntro_file_nm());
		clubParams.put("intro_save_file_nm", clubVO.getIntro_save_file_nm());
		clubParams.put("poster_file_nm", clubVO.getPoster_file_nm());
		clubParams.put("poster_save_file_nm", clubVO.getPoster_save_file_nm());
		clubParams.put("register_cd", "008003");

		try {
			clubService.createClub(clubParams);
		} catch (Exception e) {
			System.err.println("[createClub.do] create club error :" + e.getMessage());
			CommonUtils.showAlert(response, "클럽 생성 오류", "index.do");
			return null;
		}
		
		Map<String, Object> memberParams = new HashMap<String, Object>();
		Date dt = new java.util.Date();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
		String today = sdf.format(dt);
		System.err.println("[createClub.do] today: " + today);
		System.err.println("[createClub.do] userVO.getGender(): " + userVO.getGender());
		
		memberParams.put("id", user_id);
		memberParams.put("staff_cd", "004001");
		memberParams.put("join_dt", today);
		memberParams.put("name", userVO.getName());
		memberParams.put("major", userVO.getMajor());
		memberParams.put("grade", userVO.getGrade());
		
		if(userVO.getGender().equals("1"))
			memberParams.put("gender_cd", "003001");
		else
			memberParams.put("gender_cd", "003002");
		
		/* 추후 제거 */
		if(userVO.getPhoneNumber() == null)
			memberParams.put("phone_no", "");
		else
			memberParams.put("phone_no", userVO.getPhoneNumber());
		
		if(userVO.getE_mail() == null)
			memberParams.put("email", "");
		else
			memberParams.put("email", userVO.getE_mail());
		
		try {
			clubMemberService.addClubMember(memberParams);
		} catch (Exception e) {
			System.err.println("[createClub.do] create clubmember error :" + e.getMessage());
			CommonUtils.showAlert(response, "clubmember 생성 오류", "index.do");
			return null;
		}
			
		CommonUtils.showAlert(response, "동아리 개설 신청 완료", "index.do");
		return null;
	}
	
	
	
		
}
