/*
 * Controlador Java de la Secretaría de Estado de Administraciones Públicas
 * para el DNI electrónico.
 *
 * El Controlador Java para el DNI electrónico es un proveedor de seguridad de JCA/JCE 
 * que permite el acceso y uso del DNI electrónico en aplicaciones Java de terceros 
 * para la realización de procesos de autenticación, firma electrónica y validación 
 * de firma. Para ello, se implementan las funcionalidades KeyStore y Signature para 
 * el acceso a los certificados y claves del DNI electrónico, así como la realización 
 * de operaciones criptográficas de firma con el DNI electrónico. El Controlador ha 
 * sido diseñado para su funcionamiento independiente del sistema operativo final.
 * 
 * Copyright (C) 2012 Dirección General de Modernización Administrativa, Procedimientos 
 * e Impulso de la Administración Electrónica
 * 
 * Este programa es software libre y utiliza un licenciamiento dual (LGPL 2.1+
 * o EUPL 1.1+), lo cual significa que los usuarios podrán elegir bajo cual de las
 * licencias desean utilizar el código fuente. Su elección deberá reflejarse 
 * en las aplicaciones que integren o distribuyan el Controlador, ya que determinará
 * su compatibilidad con otros componentes.
 *
 * El Controlador puede ser redistribuido y/o modificado bajo los términos de la 
 * Lesser GNU General Public License publicada por la Free Software Foundation, 
 * tanto en la versión 2.1 de la Licencia, o en una versión posterior.
 * 
 * El Controlador puede ser redistribuido y/o modificado bajo los términos de la 
 * European Union Public License publicada por la Comisión Europea, 
 * tanto en la versión 1.1 de la Licencia, o en una versión posterior.
 * 
 * Debería recibir una copia de la GNU Lesser General Public License, si aplica, junto
 * con este programa. Si no, consúltelo en <http://www.gnu.org/licenses/>.
 * 
 * Debería recibir una copia de la European Union Public License, si aplica, junto
 * con este programa. Si no, consúltelo en <http://joinup.ec.europa.eu/software/page/eupl>.
 *
 * Este programa es distribuido con la esperanza de que sea útil, pero
 * SIN NINGUNA GARANTÍA; incluso sin la garantía implícita de comercialización
 * o idoneidad para un propósito particular.
 */
package es.gob.jmulticard.ui.passwordcallback.gui;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Image;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.image.BufferedImage;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;

/** Adaptador de componentes para su redimensionamiento.
 * @author INTECO */
final class ResizingAdaptor extends ComponentAdapter {
    private final JAccessibilityCustomDialog theCustomDialog;

    /** Constructor
     * @param window
     *        ventana a redimensionar */
    ResizingAdaptor(final JAccessibilityCustomDialog customDialog) {
        super();
        this.theCustomDialog = customDialog;
    }

    /** Evento de redimensionado */
    @Override
    public void componentResized(final ComponentEvent e) {
        if (this.theCustomDialog != null) {
            this.adjustFontSize(this.theCustomDialog.getComponents());
        }
    }

    /** Ajusta el tamano de fuente de una ventana
     * @param components */
    private void adjustFontSize(final Component[] components) {
        // Se calcula la relacion de aspecto para redimensionar el texto
        double relWidth;
        double relHeight;
        float relation = 1;
        if (this.theCustomDialog != null) {
            // Se comprueba si esta activado el modo negrita, fuente grande o si es necesario que la ventana sea grande por defecto
            if (GeneralConfig.isBigFontSize() || GeneralConfig.isFontBold() || this.theCustomDialog.isBigSizeDefault()) {
                relWidth = this.theCustomDialog.getSize().getWidth() / this.theCustomDialog.getInitialWidth();
                relHeight = this.theCustomDialog.getSize().getHeight() / this.theCustomDialog.getInitialHeight();
            }
            else {
                relWidth = this.theCustomDialog.getSize().getWidth() / this.theCustomDialog.getInitialWidth();
                relHeight = this.theCustomDialog.getSize().getHeight() / this.theCustomDialog.getInitialHeight();
            }
            relation = Math.round(relWidth * relHeight * this.theCustomDialog.getMinimumRelation());
        }

        for (final Component component : components) {
            final Component actualComponent = component;
            if (isResizable(actualComponent)) {
                if (relation > 10) {
                    float resizeFactor = 0;
                    if (this.theCustomDialog != null) {
                        resizeFactor = Math.round(relation / getResizingFactorCustomDialog());
                    }
                    else {
                        resizeFactor = Math.round(relation / getResizingFactorFileChooser());
                    }
                    actualComponent.setFont(actualComponent.getFont().deriveFont((getFontSize() + resizeFactor)));
                }
                else {
                    if (actualComponent instanceof JComboBox) {
                        // TODO Workaround buscar solucion mejor
                        actualComponent.setPreferredSize(new Dimension(100, 25));
                    }
                    actualComponent.setFont(actualComponent.getFont().deriveFont(getFontSize()));
                }
            }
            // Caso de borde con texto
            if (actualComponent instanceof JPanel) {
                final Border componentBorder = ((JPanel) actualComponent).getBorder();
                if (componentBorder instanceof TitledBorder) {
                    // Se comprueba si el panel tiene un nombre asignado
                    final String name = actualComponent.getName();
                    // Se hara el resize del titulo en el caso de que el componente no sea el panel de botones de accesibilidad de los alerts
                    if ((name == null) || (!name.equalsIgnoreCase("AccessibilityButtonsPanel"))) { //$NON-NLS-1$
                        final TitledBorder b = (TitledBorder) componentBorder;
                        final float resizeFactor = Math.round(relation / getResizingFactorFrame());
                        if (b.getTitleFont() != null) {
                            b.setTitleFont(b.getTitleFont().deriveFont((getFontSize() - 2 + resizeFactor)));
                        }
                        else {
                            b.setTitleFont(actualComponent.getFont().deriveFont((getFontSize() - 2 + resizeFactor)));
                        }
                    }
                }
            }

            if (actualComponent instanceof Container) {
                if (!(actualComponent instanceof JComboBox)) {
                    // Si nos encontramos con un contenedor, redimensionamos sus hijos
                    final Container actualContainer = (Container) actualComponent;
                    adjustFontSize(actualContainer.getComponents());
                }
            }
            // Redimensionado de una etiqueta con icono
            if (actualComponent instanceof IconLabel) {
                final int multiplicando = 4;
                final IconLabel iconLabel = (IconLabel) actualComponent;
                if (iconLabel.getOriginalIcon() != null) {
                    final float resizeFactor = getImageResizeFactor(Constants.RESIZING_IMAGES_FACTOR);

                    // Se obtienen las dimensiones del icono original
                    final int w = iconLabel.getOriginalDimension().width;
                    final int h = iconLabel.getOriginalDimension().height;
                    // Se hace el resize de la imagen
                    resizeImage(resizeFactor, actualComponent, w, h, multiplicando);
                }
            }

            // imagenes dentro de JButton
            if (actualComponent instanceof JButton && ((JButton) actualComponent).getIcon() != null) {
                float resizeFactor = 0;
                if (this.theCustomDialog != null) {
                    resizeFactor = getImageResizeFactor(Constants.RESIZING_IMAGES_FACTOR + 0.0010);
                }
                else {
                    resizeFactor = getImageResizeFactor(Constants.RESIZING_IMAGES_FACTOR);
                }

                resizeImageButton(resizeFactor, actualComponent);
            }
        }
    }

    /** Devuelve el factor final de redimensionado de imagen.
     * @param height altura
     * @param factor factor
     * @return factor final de redimensionado */
    private float getImageResizeFactor(final double factor) {
        float resizeFactor = 0;
        if (this.theCustomDialog != null) {
            resizeFactor = (float) (this.theCustomDialog.getHeight() * factor);
        }
        return resizeFactor;
    }

    private static Image iconToImage(final Icon icon){
    	return iconToImage(icon, new Dimension(icon.getIconWidth(), icon.getIconWidth()));
    }
    
    private static Image iconToImage(final Icon icon, Dimension d) {
        if (icon instanceof ImageIcon) {
            return ((ImageIcon) icon).getImage();
        }
        final int w = d.width;
        final int h = d.height;
        final GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        final GraphicsDevice gd = ge.getDefaultScreenDevice();
        final GraphicsConfiguration gc = gd.getDefaultConfiguration();
        final BufferedImage image = gc.createCompatibleImage(w, h);
        final Graphics2D g = image.createGraphics();
        icon.paintIcon(null, g, 0, 0);
        g.dispose();
        return image;
    }

    /** Redimensiona una imagen
     * @param factor factor de redimension
     * @param c Componente de tipo JLabel en el que se encuentra la imagen
     * @param w Width inicial de la imagen
     * @para h Height inicial de la imagen
     * @param multiplicando Valor de multiplicacion para el nuevo tamano de la imagen. Es mayor cuanto menor sea el tamano inicial de la imagen */
    private static void resizeImage(final double factor, final Component c, final int w, final int h, final int multiplicando) {
        Image image = null;
        // Se comprueba si el componente es instancia de IconLabel
        if (c instanceof IconLabel) {
            final IconLabel iconLabel = (IconLabel) c;
            // Se selecciona la imagen original del icono para hacer el resize
            image = iconToImage(iconLabel.getOriginalIcon(), iconLabel.getOriginalDimension());
        }
        else {
            image = iconToImage(((JLabel) c).getIcon());
        }
        final ImageIcon newImage = new ImageIcon(image.getScaledInstance((int) Math.round(w * multiplicando * factor),
                                                      (int) Math.round(h * multiplicando * factor),
                                                      java.awt.Image.SCALE_SMOOTH));
        ((JLabel) c).setIcon(newImage);
    }

    /** Redimensiona una imagen contenida en un JButton
     * @param factor factor de redimensi&oacute;n
     * @param c Componente de tipo JButton en el que se encuentra la imagen */
    private final static void resizeImageButton(final double factor, final Component c) {
        final JButton button = (JButton) c;
        ImageIcon imageIcon = null;

        // Se almacena el factor
        double factorAux = factor;

        // Se comprueba si se trata del boton de ayuda
        if ((button.getName() != null) && (button.getName().equalsIgnoreCase("maximizar"))) { //$NON-NLS-1$
            imageIcon = Constants.IMAGEICON_MAXIMIZE; // Se carga la imagen original
        }
        else if ((button.getName() != null) && (button.getName().equalsIgnoreCase("restaurar"))) { //$NON-NLS-1$
            imageIcon = Constants.IMAGEICONRESTORE; // Se carga la imagen original
        }
        else {
            imageIcon = new ImageIcon(iconToImage(button.getIcon())); // Se carga la imagen del componente actual
        }
        // Se redimensionan las imagenes
        int lado = (int) Math.round(25 * 2 * factorAux);
        lado = (lado < 25 ? 25 : lado);
        final ImageIcon newImage = new ImageIcon(imageIcon.getImage().getScaledInstance(lado,
                                                                     lado,
                                                                     java.awt.Image.SCALE_SMOOTH));
        button.setIcon(newImage);

        button.setPreferredSize(new Dimension(lado, lado));
    }

    /** Devuelve el tamano de la fuente en funcion de las opciones de accesibilidad
     * @return */
    private static float getFontSize() {
        if (GeneralConfig.isBigFontSize()) {
            return 16;
        }
        return 14;
    }

    /** Identifica los componentes de una ventana para los que se van a realizar el redimensionado.
     * @param a Componente para el que se va a comprobar si se va a redimensionar.
     * @return Boolean que indica si el componente pasado como par&aacute;metro va a ser redimensionado. */
    private static boolean isResizable(final Component a) {
        boolean resizable = false;
        resizable = resizable || (a instanceof JButton) || (a instanceof JLabel);
        resizable = resizable || (a instanceof JTextField);
        resizable = resizable || (a instanceof JPanel);
        return resizable || (a instanceof JCheckBox);
    }

    /** Indica el factor de redimensionado que se aplicara en los componentes de un JFrame. Este metodo es util para aplicar factores distintos a
     * distinto componentes.
     * @return Float con el factor a aplicar. */
    private static float getResizingFactorFrame() {
        return 3f;
    }

    /** Indica el factor de redimensionado que se aplicara en los componentes de un JFileChooser. Este metodo es util para aplicar factores distintos a
     * distinto componentes.
     * @return Float con el factor a aplicar. */
    private static float getResizingFactorFileChooser() {
        return 3f;
    }

    /** Indica el factor de redimensionado que se aplicara en los componentes de un CustomDialog. Este metodo es util para aplicar factores distintos a
     * distinto componentes.
     * @return Float con el factor a aplicar. */
    private static float getResizingFactorCustomDialog() {
        return 2f;
    }
}