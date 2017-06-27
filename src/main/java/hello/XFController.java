package hello;

import com.iflytek.cloud.speech.*;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

/**
 * Created by Administrator on 2017/6/19.
 */
@RestController
public class XFController {
    @RequestMapping("/")
    public String make(@RequestParam(value = "message", defaultValue = "测试讯飞语音合成，中国共产党万岁") String message) {
        SpeechUtility.createUtility(SpeechConstant.APPID + "= 59252d06");
        //1.创建 SpeechSynthesizer 对象
        SpeechSynthesizer mTts = SpeechSynthesizer.createSynthesizer();
//2.合成参数设置，详见《MSC Reference Manual》 SpeechSynthesizer 类
        mTts.setParameter(SpeechConstant.VOICE_NAME, "xiaoyan");//设置发音人
        mTts.setParameter(SpeechConstant.SPEED, "50");//设置语速，范围 0~100
        mTts.setParameter(SpeechConstant.PITCH, "50");//设置语调，范围 0~100
        mTts.setParameter(SpeechConstant.VOLUME, "50");//设置音量，范围 0~100

        String name = UUID.randomUUID().toString();
//3.开始合成
//设置合成音频保存位置（可自定义保存位置），默认保存在“./tts_test.pcm”
        SynthesizeToUriListener synthesizeToUriListener = new SynthesizeToUriListener() {
            //progress为合成进度0~100
            public void onBufferProgress(int progress) {
            }

            //会话合成完成回调接口
//uri为合成保存地址， error为错误信息，为null时表示合成会话成功
            public void onSynthesizeCompleted(String uri, SpeechError error) {
                if (error == null) {
                    PcmToWavUtil ptw = new PcmToWavUtil();
                    try {
                        String str = ptw.pcmToWav(uri, String.format("%s.wav", uri));
                        System.out.println(uri + str);
                        Runtime run = Runtime.getRuntime();
                        Process p = run.exec(String.format("/audio/lame -b128 %s %s.mp3", str, str));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    System.out.println(error);
                }
            }

            @Override
            public void onEvent(int i, int i1, int i2, int i3, Object o, Object o1) {

            }
        };
        mTts.synthesizeToUri(message, String.format("/audio/%s.pcm", name), synthesizeToUriListener);
        return name;
    }
}
