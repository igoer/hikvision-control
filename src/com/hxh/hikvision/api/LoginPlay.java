package com.hxh.hikvision.api;

import org.apache.log4j.Logger;

import com.sun.jna.NativeLong;
import com.sun.jna.Pointer;
import com.sun.jna.ptr.IntByReference;

/**
 * 登录到摄像头
 * 
 * @author hxh
 *
 */
public class LoginPlay {
	
	/**
	 * 全局HCNetSDK对象
	 */
	public static HCNetSDK hCNetSDK = HCNetSDK.INSTANCE;
	
	private static Logger log = Logger.getLogger(LoginPlay.class);
	
	/**
	 * 设备参数信息
	 */
	private HCNetSDK.NET_DVR_DEVICEINFO_V30 m_strDeviceInfo;
	
	/**
	 * 设备IP参数
	 */
	private HCNetSDK.NET_DVR_IPPARACFG m_strIpparaCfg;
	
	/**
	 * 用户参数
	 */
	private HCNetSDK.NET_DVR_CLIENTINFO m_strClientInfo;
	
	/**
	 * 用户句柄
	 */
	private NativeLong lUserID;
	
	/**
	 * 预览句柄
	 */
	private NativeLong lPreviewHandle;
	
	/**
	 * 登录到设备
	 */
	public boolean doLogin(String ip, short port, String username, String password){
		boolean initSuc = hCNetSDK.NET_DVR_Init();
		if (initSuc != true){
			log.error("hCNetSDK初始化失败!");
		}
	      
		m_strDeviceInfo = new HCNetSDK.NET_DVR_DEVICEINFO_V30();
		//获取用户句柄
		lUserID = hCNetSDK.NET_DVR_Login_V30(ip, port, username, password, m_strDeviceInfo);
		
		long userID = lUserID.longValue();
		if(userID == -1){
			log.error(ip + " 登录失败!");
			return false;
		} else {
			log.info(ip + " 登录成功!");
		}
		
		//保存到登录缓存中, 下次不必再进行登录操作
		//获取设备预览句柄
		NativeLong lRealHandle = play();
		//获取设备通道句柄
		NativeLong lChannel = new NativeLong(getChannel());
		//保存到缓存中
		TempData.getTempData().setNativeLong(ip, lUserID, lRealHandle, lChannel);
		
		return true;
	}
	
	/**
	 * 获取设备通道
	 */
	private int getChannel(){
		//获取IP接入配置参数
		IntByReference ibrBytesReturned = new IntByReference(0);
        boolean bRet = false;

        m_strIpparaCfg = new HCNetSDK.NET_DVR_IPPARACFG();
        m_strIpparaCfg.write();
        Pointer lpIpParaConfig = m_strIpparaCfg.getPointer();
        bRet = hCNetSDK.NET_DVR_GetDVRConfig(lUserID, HCNetSDK.NET_DVR_GET_IPPARACFG, new NativeLong(0), lpIpParaConfig, m_strIpparaCfg.size(), ibrBytesReturned);
        m_strIpparaCfg.read();
        
        if(!bRet){
        	log.info("设备支持IP通道?  No");
        	for (int iChannum = 0; iChannum < m_strDeviceInfo.byChanNum; iChannum++) {
        		log.info("通道号: " + iChannum + m_strDeviceInfo.byStartChan);
            }
        	if(m_strDeviceInfo.byChanNum > 0){
        		return Integer.valueOf(0 + m_strDeviceInfo.byStartChan);
        	}
        } else {
        	log.info("设备支持IP通道?  Yes");
        }
        return -1;
	}
	
	/**
	 * 获取预览句柄
	 * 
	 * @return
	 */
	public NativeLong play(){
		//获取通道号
        int iChannelNum = getChannel();//通道号
        if(iChannelNum == -1) {
        	log.error("没有获取到预览通道!");
            return null;
        }
        
        m_strClientInfo = new HCNetSDK.NET_DVR_CLIENTINFO();
        m_strClientInfo.lChannel = new NativeLong(iChannelNum);
        
        lPreviewHandle = hCNetSDK.NET_DVR_RealPlay_V30(lUserID, m_strClientInfo, null, null, true);
        log.info("获取预览 SUCCESS!");
        
        return lPreviewHandle;
	}
	
	/**
	 * 获取用户句柄
	 * 
	 * @return
	 */
	public NativeLong user() {
		return lUserID;
	}
}
