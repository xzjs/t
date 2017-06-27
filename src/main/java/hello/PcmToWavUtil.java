package hello;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

//import org.apache.log4j.Logger;

public class PcmToWavUtil {
//	private static Logger logger = Logger.getLogger(PcmToWavUtil.class);

	/**
	 * 把pcm 格式文件转成wav mp3 amr等格式
	 * 
	 * @param src
	 *            需要转换的文件（包含路径）
	 * @param target
	 *            转换成功保存的路径（包含文件名）
	 * @throws Exception
	 */
	public String pcmToWav(String src, String target) throws Exception {
		FileInputStream fis = new FileInputStream(src);
		FileOutputStream fos = new FileOutputStream(target);

		// 计算长度
		byte[] buf = new byte[1024 * 4];
		int size = fis.read(buf);
		int PCMSize = 0;
		while (size != -1) {
			PCMSize += size;
			size = fis.read(buf);
		}
		fis.close();

		// 填入参数，比特率等等。这里用的是16位单声道 16000 hz
		WaveHeader header = new WaveHeader();
		header.fileLength = PCMSize + (44 - 8);
		header.FmtHdrLeth = 16;
		header.BitsPerSample = 16;
		header.Channels = 1;
		header.FormatTag = 1;
		header.SamplesPerSec = 16000;
		header.BlockAlign = (short) (header.Channels * header.BitsPerSample / 8);
		header.AvgBytesPerSec = header.BlockAlign * header.SamplesPerSec;
		header.DataHdrLeth = PCMSize;

		byte[] h = header.getHeader();
		assert h.length == 44; // WAV标准，头部应该是44字节
		// write header
		fos.write(h, 0, h.length);
		// write data stream
		fis = new FileInputStream(src);
		size = fis.read(buf);
		while (size != -1) {
			fos.write(buf, 0, size);
			size = fis.read(buf);
		}
		fis.close();
		fos.close();
//		logger.info("转换成格式成功,文件路径：" + target);
		return target;
	}

	// WavHeader辅助类。用于生成头部信息。
	public class WaveHeader {
		public final char fileID[] = { 'R', 'I', 'F', 'F' };
		public int fileLength;
		public char wavTag[] = { 'W', 'A', 'V', 'E' };;
		public char FmtHdrID[] = { 'f', 'm', 't', ' ' };
		public int FmtHdrLeth;
		public short FormatTag;
		public short Channels;
		public int SamplesPerSec;
		public int AvgBytesPerSec;
		public short BlockAlign;
		public short BitsPerSample;
		public char DataHdrID[] = { 'd', 'a', 't', 'a' };
		public int DataHdrLeth;

		public byte[] getHeader() throws IOException {
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			WriteChar(bos, fileID);
			WriteInt(bos, fileLength);
			WriteChar(bos, wavTag);
			WriteChar(bos, FmtHdrID);
			WriteInt(bos, FmtHdrLeth);
			WriteShort(bos, FormatTag);
			WriteShort(bos, Channels);
			WriteInt(bos, SamplesPerSec);
			WriteInt(bos, AvgBytesPerSec);
			WriteShort(bos, BlockAlign);
			WriteShort(bos, BitsPerSample);
			WriteChar(bos, DataHdrID);
			WriteInt(bos, DataHdrLeth);
			bos.flush();
			byte[] r = bos.toByteArray();
			bos.close();
			return r;
		}

		private void WriteShort(ByteArrayOutputStream bos, int s)
				throws IOException {
			byte[] mybyte = new byte[2];
			mybyte[1] = (byte) ((s << 16) >> 24);
			mybyte[0] = (byte) ((s << 24) >> 24);
			bos.write(mybyte);
		}

		private void WriteInt(ByteArrayOutputStream bos, int n)
				throws IOException {
			byte[] buf = new byte[4];
			buf[3] = (byte) (n >> 24);
			buf[2] = (byte) ((n << 8) >> 24);
			buf[1] = (byte) ((n << 16) >> 24);
			buf[0] = (byte) ((n << 24) >> 24);
			bos.write(buf);
		}

		private void WriteChar(ByteArrayOutputStream bos, char[] id) {
			for (int i = 0; i < id.length; i++) {
				char c = id[i];
				bos.write(c);
			}
		}
	}
}