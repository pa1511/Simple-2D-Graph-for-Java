package utility;


import java.awt.Image;
import java.io.IOException;
import java.net.URL;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;

public class ResourceLoader {
	
	private ResourceLoader() {
	}
	
	@SuppressWarnings("rawtypes")
	public static @CheckForNull Image loadImage(@Nonnull Class clazzUsedForReltivePath, @Nonnull String imageName) throws IOException{
		final @CheckForNull URL url = clazzUsedForReltivePath.getResource(imageName);
		if(url==null)
			return null;
		return ImageIO.read(url);
	}

	@SuppressWarnings("rawtypes")
	public static @CheckForNull ImageIcon loadImageIcon(@Nonnull Class clazzUsedForReltivePath, @Nonnull String imageName) throws IOException {
		final Image image = loadImage(clazzUsedForReltivePath,imageName);
		if(image==null){
			return null;
		}
		return new ImageIcon(image);
	}
	
	@SuppressWarnings("rawtypes")
	public static @CheckForNull ImageIcon loadImageIcon(@Nonnull Class clazzUsedForReltivePath, @Nonnull String imageName,@Nonnegative int width,@Nonnegative int height) throws IOException {
		final ImageIcon imageIcon = loadImageIcon(clazzUsedForReltivePath, imageName);
		if(imageIcon==null)
			return null;
		return new ImageIcon(imageIcon.getImage().getScaledInstance(width, height, Image.SCALE_SMOOTH));
	}
}
