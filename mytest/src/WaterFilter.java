import java.awt.image.BufferedImage;

public class WaterFilter extends AbstractBufferedImageOp {
	private float wavelength = 16;
	// �ݼ�����
	private float amplitude = 10;
	private float phase = 0;
	// ������ʼλ��--�м�
	private float centreX = 0.5f;
	private float centreY = 0.5f;
	// ˮ���뾶
	private float radius = 50;

	private float radius2 = 0;
	private float icentreX;
	private float icentreY;

	public WaterFilter() {}
	
	public WaterFilter(float x, float y) {
		
	}

	@Override
	public BufferedImage filter(BufferedImage src, BufferedImage dest) {
		int width = src.getWidth();
		int height = src.getHeight();

		if (dest == null)
			dest = createCompatibleDestImage(src, null);

		int[] inPixels = new int[width * height];
		int[] outPixels = new int[width * height];
		getRGB(src, 0, 0, width, height, inPixels);
		icentreX = width * centreX;
		icentreY = height * centreY;
		if (radius == 0)
			radius = Math.min(icentreX, icentreY);
		radius2 = radius * radius;
		int index = 0;
		float[] out = new float[2];
		for (int row = 0; row < height; row++) {
			for (int col = 0; col < width; col++) {
				index = row * width + col;

				// ��ȡˮ������ɢλ�ã�����Ҫ��һ��
				generateWaterRipples(col, row, out);
				int srcX = (int) Math.floor(out[0]);
				int srcY = (int) Math.floor(out[1]);
				float xWeight = out[0] - srcX;
				float yWeight = out[1] - srcY;
				int nw, ne, sw, se;

				// ��ȡ��Χ�ĸ����أ���ֵ�ã�
				if (srcX >= 0 && srcX < width - 1 && srcY >= 0
						&& srcY < height - 1) {
					// Easy case, all corners are in the image
					int i = width * srcY + srcX;
					nw = inPixels[i];
					ne = inPixels[i + 1];
					sw = inPixels[i + width];
					se = inPixels[i + width + 1];
				} else {
					// Some of the corners are off the image
					nw = getPixel(inPixels, srcX, srcY, width, height);
					ne = getPixel(inPixels, srcX + 1, srcY, width, height);
					sw = getPixel(inPixels, srcX, srcY + 1, width, height);
					se = getPixel(inPixels, srcX + 1, srcY + 1, width, height);
				}

				// ȡ�ö�Ӧ�����λ��P(x, y)�����أ�ʹ��˫���Բ�ֵ
				/*
				 * if(xWeight >=0 || yWeight >= 0) { outPixels[index] =
				 * ImageMath.bilinearInterpolate(xWeight, yWeight, nw, ne, sw,
				 * se); } else { outPixels[index] = inPixels[index]; }
				 */
				outPixels[index] = ImageMath.bilinearInterpolate(xWeight, yWeight, nw, ne, sw, se);
			}
		}

		setRGB(dest, 0, 0, width, height, outPixels);
		return dest;
	}

	private int getPixel(int[] pixels, int x, int y, int width, int height) {
		if (x < 0 || x >= width || y < 0 || y >= height) {
			return 0; // �е㱩���������ù���
		}
		return pixels[y * width + x];
	}

	protected void generateWaterRipples(int x, int y, float[] out) {
		float dx = x - icentreX;
		float dy = y - icentreY;
		float distance2 = dx * dx + dy * dy;
		// ȷ�� water ripple�İ뾶������ڰ뾶֮�⣬��ֱ�ӻ�ȡԭ��λ�ã����ü���Ǩ����
		if (distance2 > radius2) {
			out[0] = x;
			out[1] = y;
		} else {
			// �����radius�뾶֮�ڣ��������
			float distance = (float) Math.sqrt(distance2);
			// ����ĵ����
			float amount = amplitude * (float) Math.sin(distance / wavelength * ImageMath.TWO_PI - phase);
			// ����������ʧ��
			amount *= (radius - distance) / radius; // ����������ʧ��
			if (distance != 0)
				amount *= wavelength / distance;
			// �õ�water ripple ����Ǩ��λ��
			out[0] = x + dx * amount;
			out[1] = y + dy * amount;
		}
	}

}