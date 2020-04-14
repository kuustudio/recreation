package com.royal.recreation.controller;

import com.royal.recreation.config.bean.MyUserDetails;
import com.royal.recreation.controller.base.BaseController;
import com.royal.recreation.controller.base.BaseQuery;
import com.royal.recreation.core.entity.*;
import com.royal.recreation.core.type.BonusLimitType;
import com.royal.recreation.core.type.PointRecordType;
import com.royal.recreation.core.type.Status;
import com.royal.recreation.core.type.UserType;
import com.royal.recreation.spring.mongo.Mongo;
import com.royal.recreation.util.Constant;
import com.royal.recreation.util.ExcelUtil;
import com.royal.recreation.util.MapUtil;
import com.royal.recreation.util.Util;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.*;

import static com.royal.recreation.util.Constant.EMPTY_QUERY;

@Controller
@RequestMapping("/team")
@PreAuthorize("hasAuthority('AGENT')")
public class TeamController extends BaseController {

    @Autowired
    private PasswordEncoder passwordEncoder;

    @RequestMapping("/rechargePool")
    @PreAuthorize("hasAuthority('ADMIN')")
    @ResponseBody
    public void rechargePool(BigDecimal input) {
        if (input == null || input.compareTo(BigDecimal.ZERO) <= 0) {
            responseError("有选项没有填写");
            return;
        }
        Mongo.buildMongo().updateFirst(update -> {
            update.inc("currentValue", input);
            update.inc("rechargeValue", input);
        }, CapitalPool.class);
    }

    @RequestMapping("/cashOutPool")
    @PreAuthorize("hasAuthority('ADMIN')")
    @ResponseBody
    public void cashOutPool(BigDecimal input) {
        if (input == null || input.compareTo(BigDecimal.ZERO) <= 0) {
            responseError("有选项没有填写");
            return;
        }
        Mongo.buildMongo().updateFirst(update -> {
            update.inc("currentValue", input.negate());
            update.inc("cashOutValue", input);
        }, CapitalPool.class);
    }

    @RequestMapping("/toSystem")
    @PreAuthorize("hasAuthority('ADMIN')")
    public Object setting(@AuthenticationPrincipal MyUserDetails userDetails, Model model) {
        UserInfo user = Mongo.buildMongo().id(userDetails.getId(), UserInfo.class);
        CapitalPool pool = Mongo.buildMongo().findOne(CapitalPool.class);
        model.addAttribute("user", user);
        model.addAttribute("pool", pool);
        Map<BonusLimitType, BigDecimal> systemLimit = pool.getSystemLimit();
        model.addAttribute("bonusLimitType", new HashMap<String, Object>() {{
            systemLimit.forEach((k, v) -> put(k.toString(), v));
        }});
        return "team/system";
    }

    @RequestMapping("/bonusLimit")
    @PreAuthorize("hasAuthority('ADMIN')")
    @ResponseBody
    public Object bonusLimit(HttpServletRequest request, PostCodeQuery query) {
        Map<BonusLimitType, BigDecimal> result = new HashMap<BonusLimitType, BigDecimal>() {{
            Map<String, BigDecimal> limitType = query.getBonusLimitType();
            for (String value : limitType.keySet()) {
                put(BonusLimitType.valueOf(value), limitType.get(value));
            }
        }};
        Mongo.buildMongo().updateFirst(update -> {
            update.set("systemLimit", result);
        }, CapitalPool.class);
        Constant.SYSTEM_LIMIT = result;
        return Collections.singletonMap("msg", "修改成功");
    }

    @RequestMapping("/setting")
    @PreAuthorize("hasAuthority('ADMIN')")
    @ResponseBody
    public Object setting(String systemName, Integer systemMaxBetPoint, String systemNotice) {
        if (StringUtils.isEmpty(systemName) || systemMaxBetPoint <= 0 || systemNotice == null) {
            return responseError("有选项没有填写");
        }
        Mongo.buildMongo().updateFirst(update -> {
            update.set("systemName", systemName);
            update.set("systemMaxBetPoint", systemMaxBetPoint);
            update.set("systemNotice", systemNotice);
        }, CapitalPool.class);
        Constant.SYSTEM_NAME = systemName;
        Constant.SYSTEM_MAX_BET_POINT = systemMaxBetPoint;
        Constant.SYSTEM_NOTICE = systemNotice;
        return Collections.singletonMap("msg", "修改成功");
    }

    @Transactional
    @RequestMapping("/remove")
    @ResponseBody
    @PreAuthorize("hasAuthority('ADMIN')")
    public Object remove(BaseQuery baseQuery) {
        for (Class clazz : Arrays.asList(AwardInfo.class, OrderInfo.class, UserPointRecord.class)) {
            Mongo query = Mongo.buildMongo();
            queryOfTime(query, baseQuery);
            query.remove(clazz);
        }
        return Collections.singletonMap("msg", "数据已清除");
    }

    @RequestMapping("/export")
    @PreAuthorize("hasAuthority('ADMIN')")
    public void export(BaseQuery baseQuery, HttpServletResponse response) throws IOException {
        response.setHeader("Content-Disposition", "attachment;filename=data.xlsx");
        response.setContentType("application/vnd.ms-excel;charset=UTF-8");
        response.setHeader("Pragma", "no-cache");
        response.setHeader("Cache-Control", "no-cache");
        response.setDateHeader("Expires", 0);
        List data = new ArrayList();
        for (Class clazz : Arrays.asList(AwardInfo.class, BonusSetting.class, OrderInfo.class, UserPointRecord.class, UserPointRecord.class)) {
            Mongo query = Mongo.buildMongo();
            queryOfTime(query, baseQuery);
            data.addAll(query.find(clazz));
        }
        ExcelUtil.exportExcelX(data, response.getOutputStream());
    }

    @RequestMapping("/doEditSetting")
    @PreAuthorize("hasAuthority('ADMIN')")
    @ResponseBody
    public Object doEditSetting(String id, BonusSetting bonusSetting) {
        Map<String, Object> updateMap = MapUtil.objectToMap(bonusSetting);
        Mongo.buildMongo().id(id).updateFirst(update -> {
            updateMap.forEach((k, v) -> {
                if (!StringUtils.isEmpty(v) && !"id".equals(k)) {
                    update.set(k, v);
                }
            });
        }, BonusSetting.class);
        return Collections.singletonMap("msg", "用户已修改");
    }

    @RequestMapping("/editSetting/{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public Object editSetting(@AuthenticationPrincipal MyUserDetails userDetails, @PathVariable String id, Model model) {
        BonusSetting setting = Mongo.buildMongo().id(id, BonusSetting.class);
        model.addAttribute("setting", setting);
        addUser(userDetails, model);
        return "team/editSetting";
    }

    @RequestMapping("/settingList")
    @PreAuthorize("hasAuthority('ADMIN')")
    public String settingList(@AuthenticationPrincipal MyUserDetails userDetails, BaseQuery baseQuery, Model model) {
        UserInfo user = Mongo.buildMongo().id(userDetails.getId(), UserInfo.class);
        settingListSearch(baseQuery, null, model);
        model.addAttribute("user", user);
        return "team/settingList";
    }

    @RequestMapping("/settingListSearch")
    @PreAuthorize("hasAuthority('ADMIN')")
    public String settingListSearch(BaseQuery baseQuery, UserType userType, Model model) {
        Mongo query = Mongo.buildMongo();
        queryOfTime(query, baseQuery);
        if (!StringUtils.isEmpty(userType)) {
            query.eq("userType", userType);
        }
        int count = (int) query.count(BonusSetting.class);
        List<BonusSetting> list = query.limit(Constant.limit, baseQuery.getPage()).find(BonusSetting.class);
        model.addAttribute("list", list);
        model.addAttribute("page", baseQuery.getPage());
        model.addAttribute("pages", count % Constant.limit == 0 ? count / Constant.limit : (count / Constant.limit + 1));
        model.addAttribute("queryString", getQueryString("/team/settingListSearch"));
        return "team/settingListBiao";
    }

    @RequestMapping("/recharge")
    @ResponseBody
    @Transactional
    public void recharge(@AuthenticationPrincipal MyUserDetails userDetails, BigDecimal input, String remark, String id) {
        if (input == null || input.compareTo(BigDecimal.ZERO) <= 0) {
            responseError("有选项没有填写");
            return;
        }
        UserInfo agent = Mongo.buildMongo().id(userDetails.getId(), UserInfo.class);
        if (agent.getUserType() != UserType.ADMIN && agent.getPoint().compareTo(input) < 0) {
            responseError("积分不足,请联系管理员充值");
            return;
        }
        UserInfo targetUser = Mongo.buildMongo().id(id, UserInfo.class);
        if (targetUser.getUserType() != UserType.ADMIN) {
            UserPointRecord cashOutRecord = new UserPointRecord();
            cashOutRecord.setUserId(userDetails.getId());
            cashOutRecord.setPointRecordType(PointRecordType.CASH_OUT);
            cashOutRecord.setValue(input.negate());
            cashOutRecord.setRemark(remark);
            Util.insertUserPoint(cashOutRecord);
        }

        UserPointRecord userPointRecord = new UserPointRecord();
        userPointRecord.setUserId(id);
        userPointRecord.setPointRecordType(PointRecordType.RECHARGE);
        userPointRecord.setValue(input);
        userPointRecord.setRemark(remark);
        Util.insertUserPoint(userPointRecord);
    }

    @RequestMapping("/cashOut")
    @ResponseBody
    @Transactional
    public void cashOut(@AuthenticationPrincipal MyUserDetails userDetails, BigDecimal input, String remark, String id) {
        if (input == null || input.compareTo(BigDecimal.ZERO) <= 0) {
            responseError("有选项没有填写");
            return;
        }
        UserInfo targetUser = Mongo.buildMongo().id(id, UserInfo.class);
        if (userDetails.getUserType() != UserType.ADMIN && !targetUser.getPId().equals(userDetails.getId())) {
            responseError("越权了,兄弟");
            return;
        }
        if (targetUser.getPoint().compareTo(input) < 0) {
            responseError("用户积分不足");
            return;
        }
        if (targetUser.getUserType() != UserType.ADMIN) {
            UserPointRecord rechargeRecord = new UserPointRecord();
            rechargeRecord.setUserId(userDetails.getId());
            rechargeRecord.setPointRecordType(PointRecordType.RECHARGE);
            rechargeRecord.setValue(input);
            rechargeRecord.setRemark(remark);
            Util.insertUserPoint(rechargeRecord);
        }

        UserPointRecord userPointRecord = new UserPointRecord();
        userPointRecord.setUserId(id);
        userPointRecord.setPointRecordType(PointRecordType.CASH_OUT);
        userPointRecord.setValue(input.negate());
        userPointRecord.setRemark(remark);
        Util.insertUserPoint(userPointRecord);
    }

    @RequestMapping("/userList")
    public String userList(@AuthenticationPrincipal MyUserDetails userDetails, BaseQuery baseQuery, Model model) {
        UserInfo user = Mongo.buildMongo().id(userDetails.getId(), UserInfo.class);
        userListSearch(userDetails, baseQuery, null, model);
        model.addAttribute("user", user);
        return "team/userList";
    }

    @RequestMapping("/userListSearch")
    public String userListSearch(@AuthenticationPrincipal MyUserDetails userDetails, BaseQuery baseQuery, String wxNo, Model model) {
        Mongo query = Mongo.buildMongo();
        if (userDetails.getUserType() == UserType.AGENT) {
            query.eq("pId", userDetails.getId());
        } else if (userDetails.getUserType() == UserType.ADMIN && !StringUtils.isEmpty(baseQuery.getAgentName())) {
            UserInfo agentUser = Mongo.buildMongo().eq("username", baseQuery.getAgentName()).findOne(UserInfo.class);
            if (agentUser != null) {
                query.eq("pId", agentUser.getId());
            } else {
                query.id(EMPTY_QUERY);
            }
        }
        queryOfTime(query, baseQuery);
        if (!StringUtils.isEmpty(baseQuery.getUsername())) {
            query.eq("username", baseQuery.getUsername());
        }
        if (!StringUtils.isEmpty(wxNo)) {
            query.fuzzy("wxNo", wxNo);
        }
        int count = (int) query.count(UserInfo.class);
        List<UserInfo> list = query.limit(Constant.limit, baseQuery.getPage()).find(UserInfo.class);
        model.addAttribute("list", list);
        model.addAttribute("page", baseQuery.getPage());
        model.addAttribute("pages", count % Constant.limit == 0 ? count / Constant.limit : (count / Constant.limit + 1));
        model.addAttribute("queryString", getQueryString("/team/userListSearch"));
        return "team/userListBiao";
    }

    @RequestMapping("/changeStatus")
    public void changeStatus(@AuthenticationPrincipal MyUserDetails userDetails, Status status, String id, HttpServletResponse response) {
        Mongo query = Mongo.buildMongo();
        if (userDetails.getUserType() == UserType.AGENT) {
            query.eq("pId", userDetails.getId());
        }
        query.id(id).updateFirst(update -> update.set("status", status), UserInfo.class);
    }

    @RequestMapping("/changePassword")
    @ResponseBody
    @PreAuthorize("hasAuthority('ADMIN')")
    public void changePassword(@AuthenticationPrincipal MyUserDetails userDetails, String input, String id) {
        if (StringUtils.isEmpty(input)) {
            responseError("密码不能为空");
            return;
        }
        Mongo.buildMongo().id(id).updateFirst(update -> update.set("password", passwordEncoder.encode(input)), UserInfo.class);
    }

    @RequestMapping("/editUser/{id}")
    public Object editUser(@AuthenticationPrincipal MyUserDetails userDetails, @PathVariable String id, Model model) {
        Mongo query = Mongo.buildMongo();
        if (userDetails.getUserType() == UserType.AGENT) {
            query.eq("userType", UserType.MEMBER).eq("pId", userDetails.getId());
        }

        UserInfo userInfo = Mongo.buildMongo().id(userDetails.getId(), UserInfo.class);
        BonusSetting bonusSetting = Mongo.buildMongo().id(userInfo.getBonusSettingId(), BonusSetting.class);
        UserInfo editUser = query.id(id, UserInfo.class);
        List<BonusSetting> settingList = Mongo.buildMongo().lte("fanDianRate", bonusSetting.getFanDianRate()).find(BonusSetting.class);
        model.addAttribute("editUser", editUser);
        model.addAttribute("settingList", settingList);
        return "team/editUser";
    }

    @RequestMapping("/doEditUser")
    @ResponseBody
    public Object doEditUser(@AuthenticationPrincipal MyUserDetails userDetails, String id, String bonusSettingId, String wxNo) {
        if (StringUtils.isEmpty(id) || StringUtils.isEmpty(bonusSettingId) || StringUtils.isEmpty(wxNo)) {
            return responseError("有选项没有填写");
        }
        BonusSetting bonusSetting = Mongo.buildMongo().id(bonusSettingId, BonusSetting.class);
        if (userDetails.getUserType() == UserType.AGENT) {
            UserInfo userInfo = Mongo.buildMongo().id(userDetails.getId(), UserInfo.class);
            BonusSetting userBonusSetting = Mongo.buildMongo().id(userInfo.getBonusSettingId(), BonusSetting.class);
            if (userBonusSetting.getFanDianRate().compareTo(bonusSetting.getFanDianRate()) < 0) {
                Mongo.buildMongo().id(userDetails.getId()).updateFirst(update -> update.set("status", Status.DEL), UserInfo.class);
                return responseError("越权了,兄弟");
            }
        }

        Mongo.buildMongo().id(id).updateFirst(update -> {
            update.set("bonusSettingId", bonusSetting.getId());
            update.set("bonusSettingName", bonusSetting.getSettingName());
            update.set("wxNo", wxNo);
        }, UserInfo.class);
        return Collections.singletonMap("msg", "用户已修改");
    }

    @RequestMapping("/addUser")
    public String addUser(@AuthenticationPrincipal MyUserDetails userDetails, Model model) {
        Mongo query = Mongo.buildMongo();
        if (userDetails.getUserType() == UserType.AGENT) {
            UserInfo userInfo = Mongo.buildMongo().id(userDetails.getId(), UserInfo.class);
            BonusSetting userBonusSetting = Mongo.buildMongo().id(userInfo.getBonusSettingId(), BonusSetting.class);
            query.lte("fanDianRate", userBonusSetting.getFanDianRate());
        }
        List<BonusSetting> bonusSettings = query.find(BonusSetting.class);
        UserInfo user = Mongo.buildMongo().id(userDetails.getId(), UserInfo.class);


        model.addAttribute("user", user);
        model.addAttribute("settingList", bonusSettings);
        return "team/addUser";
    }

    @RequestMapping("/doAddUser")
    @ResponseBody
    public Object doAddUser(@AuthenticationPrincipal MyUserDetails userDetails, UserInfo userInfo) {
        if (userInfo.getUserType() == null) {
            userInfo.setUserType(UserType.MEMBER);
        }
        userInfo.setPId(userDetails.getId());
        userInfo.setBonusSettingName(" ");
        if (MapUtil.objectToMap(userInfo).values().stream().anyMatch(StringUtils::isEmpty)) {
            return responseError("有选项没有填写");
        }
        UserInfo exit = Mongo.buildMongo().eq("username", userInfo.getUsername()).findOne(UserInfo.class);
        if (exit != null) {
            return responseError("账号已存在");
        }

        BonusSetting bonusSetting = Mongo.buildMongo().id(userInfo.getBonusSettingId(), BonusSetting.class);
        if (userDetails.getUserType().ordinal() >= userInfo.getUserType().ordinal()) {
            Mongo.buildMongo().id(userDetails.getId()).updateFirst(update -> update.set("status", Status.DEL), UserInfo.class);
            return responseError("越权了,兄弟");
        }
        if (userDetails.getUserType() == UserType.AGENT) {
            UserInfo agentInfo = Mongo.buildMongo().id(userDetails.getId(), UserInfo.class);
            BonusSetting agentBonusSetting = Mongo.buildMongo().id(agentInfo.getBonusSettingId(), BonusSetting.class);
            if (agentBonusSetting.getFanDianRate().compareTo(bonusSetting.getFanDianRate()) < 0) {
                Mongo.buildMongo().id(userDetails.getId()).updateFirst(update -> update.set("status", Status.DEL), UserInfo.class);
                return responseError("越权了,兄弟");
            }
        }
        userInfo.setBonusSettingName(bonusSetting.getSettingName());
        userInfo.setPassword(passwordEncoder.encode(userInfo.getPassword()));
        Mongo.buildMongo().insert(userInfo);
        return Collections.singletonMap("msg", "用户已添加");
    }

    @RequestMapping("/addBonusSetting")
    @ResponseBody
    @PreAuthorize("hasAuthority('ADMIN')")
    public Object addBonusSetting(BonusSetting bonusSetting) {
        if (MapUtil.objectToMap(bonusSetting).values().stream().anyMatch(StringUtils::isEmpty)) {
            return responseError("有选项没有填写");
        }
        Mongo.buildMongo().insert(bonusSetting);
        return Collections.singletonMap("msg", "规则添加成功");
    }

}
