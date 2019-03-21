package com.zhizhuotec.android.controller;

import java.io.File;

import java.io.OutputStream;
import java.util.HashMap;

import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.dubbo.config.annotation.Reference;
import com.zhizhuotec.common.pojo.Constant;
import com.zhizhuotec.common.pojo.Result;
import com.zhizhuotec.common.utils.AliYunSms;
import com.zhizhuotec.common.utils.UpUtils;
import com.zhizhuotec.common.utils.Utils;
import com.zhizhuotec.user.entity.User;
import com.zhizhuotec.user.entity.UserAuths;
import com.zhizhuotec.user.service.UserService;

@Controller
@RequestMapping("user")
public class UserController extends BaseController {

	@Reference
	private UserService userService;

	// 用户登录
	@RequestMapping(value = "login", method = RequestMethod.GET)
	@ResponseBody
	public Result login(UserAuths userAuths) {
		if (Utils.verifyPhone(userAuths.getIdentifier()) && Utils.verifyPass(userAuths.getCredential())) {
			userAuths.setIdentityType((String) Constant.IDENTITYTYPE_PHONE.getValue());
			userAuths.setLoginIp(getIp());
			Map<String, Object> map = new HashMap<String, Object>();
			Map<String, Object> map2 = userService.login(userAuths);
			int res = (int) map2.get("res");
			if (res == 1) {
				String token = getToken((String) map2.get("id"));
				if (token != null) {
					map.put("res", 1); // 登录成功
					map2.put("token", token);
					map.put("userInfo", map2);
				} else {
					map.put("res", 0); // 登录失败
				}
			} else {
				map.put("res", res); // 返回res
			}
			return Result.ok(map);
		} else {
			return Result.build("111", "参数校验不通过");
		}
	}

	// 用户注册
	@ResponseBody
	@RequestMapping(value = "register", method = RequestMethod.POST)
	public Result register(UserAuths userAuths, String code) {
		if (code != null && Utils.verifyPhone(userAuths.getIdentifier())
				&& Utils.verifyPass(userAuths.getCredential())) {
			Map<String, Object> map = new HashMap<String, Object>();
			String key = userAuths.getIdentifier() + "_" + Constant.CODE_TYPE_1.getValue();
			if (redisService.ttl(key) > 0 && code.equals(redisService.get(key))) {
				userAuths.setIdentityType((String) Constant.IDENTITYTYPE_PHONE.getValue());
				userAuths.setRegisterIp(getIp());
				Map<String, Object> map2 = userService.register(userAuths);
				if (map2.get("id") != null) {
					String token = getToken((String) map2.get("id"));
					if (token != null) {
						map.put("res", 1); // 注册成功
						map2.put("token", token);
						map.put("userInfo", map2);
					} else {
						map.put("res", 0); // 注册失败
					}
				} else {
					map.put("res", 3); // error:注册失败,用户已存在
				}
			} else {
				map.put("res", 2); // error:验证码错误
			}
			return Result.ok(map);
		} else {
			return Result.build("111", "参数校验不通过");
		}
	}

	// 发送验证码(type = 1/注册,修改手机 2/忘记密码)
	@RequestMapping(value = "code", method = RequestMethod.PUT)
	@ResponseBody
	public Result code(UserAuths userAuths, Integer type) {
		if (type != null && (type == 1 || type == 2) && Utils.verifyPhone(userAuths.getIdentifier())) {
			Map<String, Object> map = new HashMap<String, Object>();
			userAuths.setIdentityType((String) Constant.IDENTITYTYPE_PHONE.getValue());
			int res = (int) userService.getMsgFromIdentifier(userAuths).get("res");
			if ((type == 1 && res == 2) || (type == 2 && res == 1)) {
				String codeValue = Utils.code();
				if (sendCodeOut(userAuths.getIdentifier())) {
					if (AliYunSms.sms(userAuths.getIdentifier(), codeValue)) {
						String codeKey = userAuths.getIdentifier() + "_" + type;
						redisService.set(codeKey, codeValue);
						redisService.expire(codeKey, (int) Constant.CODE_EXPIRE.getValue());
						map.put("res", 1); // success
					} else {
						map.put("res", 0); // error:验证码发送失败
					}
				} else {
					map.put("res", 5); // error:每天信息发送超限
				}
			} else {
				if (type == 1) {
					map.put("res", 4);
				} else {
					map.put("res", res); // 2/账号不存在 3/账号已被停用 4/账号已存在,请登录
				}
			}
			return Result.ok(map);
		} else {
			return Result.build("111", "参数校验不通过");
		}
	}

	// 验证验证码
	@RequestMapping(value = "verifyCode", method = RequestMethod.GET)
	@ResponseBody
	public Result verifyCode(UserAuths userAuths, String code, Integer type) {
		if (code != null && type != null && (type == 1 || type == 2) && Utils.verifyPhone(userAuths.getIdentifier())) {
			Map<String, Object> map = new HashMap<String, Object>();
			String codeKey = userAuths.getIdentifier() + "_" + type;
			if (redisService.ttl(codeKey) > 0 && code.equals(redisService.get(codeKey))) {
				map.put("res", 1); // success
			} else {
				map.put("res", 0); // error:验证码错误
			}
			return Result.ok(map);
		} else {
			return Result.build("111", "参数校验不通过");
		}
	}

	// 获取验证码剩余时间
	@RequestMapping(value = "codeTime", method = RequestMethod.GET)
	@ResponseBody
	public Result codeTime(UserAuths userAuths, Integer type) {
		if (type != null && (type == 1 || type == 2) && Utils.verifyPhone(userAuths.getIdentifier())) {
			Map<String, Object> map = new HashMap<String, Object>();
			String codeKey = userAuths.getIdentifier() + "_" + type;
			long ttl = redisService.ttl(codeKey);
			if (ttl > 0) {
				map.put("res", 1);
				map.put("time", ttl);
			} else {
				map.put("res", 0); // error:验证码不存在或已过期
			}
			return Result.ok(map);
		} else {
			return Result.build("111", "参数校验不通过");
		}
	}

	// 忘记密码
	@ResponseBody
	@RequestMapping(value = "forgetPass", method = RequestMethod.PUT)
	public Result forgetPass(UserAuths userAuths, String code, String newPass) {
		if (code != null && newPass != null && Utils.verifyPhone(userAuths.getIdentifier())
				&& Utils.verifyPass(newPass)) {
			Map<String, Object> map = new HashMap<String, Object>();
			String codeKey = userAuths.getIdentifier() + "_" + Constant.CODE_TYPE_2.getValue();
			if (redisService.ttl(codeKey) > 0 && code.equals(redisService.get(codeKey))) {
				userAuths.setIdentityType((String) Constant.IDENTITYTYPE_PHONE.getValue());
				map.put("res", userService.forgetPass(userAuths, newPass)); // 返回res
			} else {
				map.put("res", 4);// error:验证码错误
			}
			return Result.ok(map);
		} else {
			return Result.build("111", "参数校验不通过");
		}
	}

	// 修改密码
	@ResponseBody
	@RequestMapping(value = "editPass", method = RequestMethod.PUT)
	public Result editPass(UserAuths userAuths, String newPass) {
		if (Utils.verifyPhone(userAuths.getIdentifier()) && Utils.verifyPass(userAuths.getCredential())
				&& Utils.verifyPass(newPass)) {
			Map<String, Object> map = new HashMap<String, Object>();
			userAuths.setIdentityType((String) Constant.IDENTITYTYPE_PHONE.getValue());
			map.put("res", userService.editPass(userAuths, newPass)); // 返回res
			return Result.ok(map);
		} else {
			return Result.build("111", "参数校验不通过");
		}
	}

	// 修改手机
	@ResponseBody
	@RequestMapping(value = "editPhone", method = RequestMethod.PUT)
	public Result editPhone(UserAuths userAuths, String token, String newPhone, String newCode) {
		String uId = getUId();
		if (uId != null) {
			if (newCode != null && Utils.verifyPass(userAuths.getCredential()) && Utils.verifyPhone(newPhone)) {
				Map<String, Object> map = new HashMap<String, Object>();
				String newCodeKey = newPhone + "_" + Constant.CODE_TYPE_1.getValue();
				if (redisService.ttl(newCodeKey) > 0 && newCode.equals(redisService.get(newCodeKey))) {
					userAuths.setUserId(uId);
					userAuths.setIdentityType((String) Constant.IDENTITYTYPE_PHONE.getValue());
					map.put("res", userService.editPhone(userAuths, newPhone)); // 返回res
				} else {
					map.put("res", 6); // error:验证码错误
				}
				return Result.ok(map);
			} else {
				return Result.build("111", "参数校验不通过");
			}
		} else {
			return Result.build("222", "登陆失效,请重新登录");
		}
	}

	// 用户注销
	@ResponseBody
	@RequestMapping(value = "logout", method = RequestMethod.DELETE)
	public Result logout() {
		String uId = getUId();
		if (uId != null) {
			Map<String, Object> map = new HashMap<String, Object>();
			if (redisService.del(uId) > 0) {
				map.put("res", 1); // 注销成功
			} else {
				map.put("res", 0); // 注销失败
			}
			return Result.ok(map);
		} else {
			return Result.build("222", "登陆失效,请重新登录");
		}
	}

	// 用户设置
	@ResponseBody
	@RequestMapping(value = "setting", method = RequestMethod.PUT)
	public Result setting(String data) {
		String uId = getUId();
		if (uId != null) {
			if (StringUtils.isNotBlank(data)) {
				Map<String, Object> map = new HashMap<String, Object>();
				map.put("res", userService.updateSetting(uId, data)); // 返回res
				return Result.ok(map);
			} else {
				return Result.build("111", "参数校验不通过");
			}
		} else {
			return Result.build("222", "登陆失效,请重新登录");
		}
	}

	// 头像上传
	@ResponseBody
	@RequestMapping(value = "uploadHeader", method = RequestMethod.PUT)
	public Result uploadHeader(String imgStr) {
		String uId = getUId();
		if (uId != null) {
			if (StringUtils.isNotBlank(imgStr)) {
				Map<String, Object> map = new HashMap<String, Object>();
				String imageName = Utils.getHash(uId, "MD5");
				User user = new User();
				user.setId(uId);
				user.setAvatar(imageName);
				if (UpUtils.uploadToBase64(imgStr, Constant.HEADER_FOLDER.getValue(),
						imageName + Constant.DEFAULT_HEADER_SUFFIX.getValue()) && userService.updateByHeader(user)) {
					map.put("res", 1); // 上传成功
					map.put("url", Constant.BASE_IP.getValue() + File.separator + "user" + File.separator
							+ "getHeader?id=" + imageName);
				} else {
					map.put("res", 0); // 上传失败
				}
				return Result.ok(map);
			} else {
				return Result.build("111", "参数校验不通过");
			}
		} else {
			return Result.build("222", "登陆失效,请重新登录");
		}
	}

	// 获取头像
	@ResponseBody
	@RequestMapping(value = "getHeader", method = RequestMethod.GET)
	public void getHeader(String id) {
		if (StringUtils.isNotBlank(id)) {
			try {
				byte[] b = UpUtils
						.download(Constant.BASE_PATH.getValue() + UpUtils.SEPARATOR + Constant.HEADER_FOLDER.getValue()
								+ UpUtils.SEPARATOR + id + Constant.DEFAULT_HEADER_SUFFIX.getValue());
				if (b != null) {
					response.addHeader("Content-Disposition",
							"attachment;filename=" + id + Constant.DEFAULT_HEADER_SUFFIX.getValue());
					response.setContentType("image/" + Constant.DEFAULT_HEADER_SUFFIX.getValue());
					OutputStream os = response.getOutputStream();
					os.write(b);
					os.flush();
					os.close();
				}
			} catch (Exception e) {

			}
		}
	}

	// 获取固件升级地址
	@ResponseBody
	@RequestMapping(value = "firmwareUpgrade", method = RequestMethod.GET)
	public Result firmwareUpgrade() {
		String uId = getUId();
		if (uId != null) {
			Map<String, Object> map = new HashMap<String, Object>();
			String fileName = UpUtils.findLastModifiedFile(Constant.BASE_PATH.getValue(),
					Constant.BLUETOOTH_FOLDER.getValue());
			if (fileName != null) {
				map.put("res", 1); // 返回固件最新版本
				map.put("version", Integer.valueOf(fileName.substring(0, fileName.indexOf("_"))));
				map.put("url", Constant.BASE_IP.getValue() + File.separator + "user" + File.separator
						+ "getFirmware?id=" + fileName);
			} else {
				map.put("res", 0); // 没有固件文件
			}
			return Result.ok(map);
		} else {
			return Result.build("222", "登陆失效,请重新登录");
		}
	}

	// 获取固件文件
	@ResponseBody
	@RequestMapping(value = "getFirmware", method = RequestMethod.GET)
	public void getFirmware(String id) {
		if (StringUtils.isNotBlank(id)) {
			try {
				byte[] b = UpUtils.download(Constant.BASE_PATH.getValue() + UpUtils.SEPARATOR
						+ Constant.BLUETOOTH_FOLDER.getValue() + UpUtils.SEPARATOR + id);
				if (b != null) {
					response.addHeader("Content-Disposition", "attachment;filename=" + id);
					response.setContentType("multipart/form-data");
					OutputStream os = response.getOutputStream();
					os.write(b);
					os.flush();
					os.close();
				}
			} catch (Exception e) {
				Utils.logger("ERROR", "Server exception：", e);
			}
		}
	}

	// 获取app升级
	@ResponseBody
	@RequestMapping(value = "getApp", method = RequestMethod.GET)
	public void getApp() {
		try {
			String fileName = UpUtils.findLastModifiedFile(Constant.BASE_PATH.getValue(),
					Constant.UPGRADE_FOLDER.getValue());
			if (fileName != null) {
				byte[] b = UpUtils.download(Constant.BASE_PATH.getValue() + UpUtils.SEPARATOR
						+ Constant.UPGRADE_FOLDER.getValue() + UpUtils.SEPARATOR + fileName);
				if (b != null) {
					response.addHeader("Content-Disposition", "attachment;filename=" + fileName);
					response.setContentType("multipart/form-data");
					OutputStream os = response.getOutputStream();
					os.write(b);
					os.flush();
					os.close();
				}
			}
		} catch (Exception e) {
			Utils.logger("ERROR", "Server exception：", e);
		}
	}
}
