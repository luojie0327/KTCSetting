package com.ktc.setting.view.restore.restoreTool;

import android.content.Context;
import android.os.SystemProperties;
import android.provider.Settings;

import com.mediatek.twoworlds.factory.MtkTvFApiSystem;
import com.mediatek.twoworlds.factory.model.MtkTvFApiUartBaudRateEnm;
import com.mediatek.twoworlds.factory.model.MtkTvFApiUartDeviceTypeEnm;
import com.mediatek.twoworlds.factory.model.MtkTvFApiUartPortTypeEnm;
import com.mediatek.twoworlds.factory.common.MtkTvFApiSystemTypes;
import com.mediatek.twoworlds.factory.MtkTvFApiPeripheral;
import com.mediatek.twoworlds.tv.MtkTvConfig;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;

public class MtkRestoreTool implements IRestore {
    private static MtkTvConfig mTvConfig = MtkTvConfig.getInstance();
    private static MtkTvFApiSystem mTvFApiSystem = MtkTvFApiSystem.getInstance();

    @Override
    public void restoreTvData(Context context) {
        mTvFApiSystem.setFactoryPowerMode(MtkTvFApiSystemTypes.EnumSystemFactoryPowerMode.values()[0]);
	mTvFApiSystem.setUartEnvironment(false);
	resetUartDebug();
       //SystemProperties.set("factory.reset", "true");
        mTvFApiSystem.setUartEnvironment(false);
        SystemProperties.set("persist.sys.autosleep.time","4");
		setIniValue("/vendor/tvconfig/config/model/Customer_1.ini", "AUTO_TEST_ON", "0");
	setIniValue("/vendor/tvconfig/config/model/Customer_1.ini", "SMT_ON", "0");
        if ("0".equals(MtkTvFApiSystem.getInstance().getEmmcEnvVar("WB_ADJUST_STATUS"))) {
            SystemProperties.set("factory.recallawb", "2");
        } else {
            SystemProperties.set("factory.recallawb", "1");
        }
	SystemProperties.set("ctl.start", "reset_service");
        mTvConfig.setConfigValue("g_factory__reset", 1);
        Settings.Global.putInt(context.getContentResolver(), "age_mode", 0);
		Settings.Global.putInt(context.getContentResolver(), "first_to_set_audio", 0);
    }

    @Override
    public void restoreAndroid(Context context) {
        AndroidRestoreTool.androidReset(context);
    }
	
	public static void setIniValue(String path, String key, String value) {
        File file = new File(path);
        if (!file.exists()) {
            return;
        }
        FileOutputStream outputStream = null;
        FileInputStream inputStream = null;
        InputStreamReader inputStreamReader = null;
        BufferedReader reader = null;
        StringBuffer sb = new StringBuffer();
        try {
            inputStream = new FileInputStream(file);
            inputStreamReader = new InputStreamReader(inputStream);
            reader = new BufferedReader(inputStreamReader);
            String s = "";
            while ((s = reader.readLine()) != null) {
                if (s.contains(key)) {
                    sb.append(key);
                    sb.append(" = ");
                    sb.append(value);
                    sb.append(";");
                    sb.append(System.getProperty("line.separator"));
                } else {
                    sb.append(s);
                    sb.append(System.getProperty("line.separator"));
                }
            }
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } finally {
            try {
                if (inputStream != null) {
                    inputStream.close();
                }
                if (inputStreamReader != null) {
                    inputStreamReader.close();
                }
                if (reader != null) {
                    reader.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        try {
            outputStream = new FileOutputStream(file, false);
            outputStream.write(sb.toString().getBytes());
            outputStream.flush();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } finally {
            try {
                if (outputStream != null) {
                    outputStream.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void resetUartDebug(){
        boolean bOnOff = false;
        byte baudrate = MtkTvFApiUartBaudRateEnm.E_MTK_TV_FAPI_UART_BAUDRATE_115200;
        byte port = MtkTvFApiUartPortTypeEnm.E_MTK_TV_FAPI_UART_PORT_0;
        byte uart = MtkTvFApiUartDeviceTypeEnm.E_MTK_TV_FAPI_UART_PIU_UART0;
        MtkTvFApiPeripheral.getInstance().setFactoryUartCallback(bOnOff, baudrate, port, uart);
    }
}
