import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.EtchedBorder;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

// ИНТЕРФЕЙС

class GUI extends JFrame {
    private JButton receive;
    private JButton reference;
    private JTextArea imageWay;
    private JPanel imagePanel;
    private BufferedImage image;

    GUI(String name) {
        super(name);
        createGUI();
    }

    private void createGUI() {
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout());

        JPanel southPanel = new JPanel();
        southPanel.setLayout(new BorderLayout());

        imageWay = new JTextArea("Вы видете кнопку 'Справка'?!");
        imageWay.setEditable(false);
        southPanel.add(BorderLayout.CENTER, imageWay);

        reference = new JButton("Справка");
        southPanel.add(BorderLayout.NORTH, reference);

        receive = new JButton("Выбрать");
        southPanel.add(BorderLayout.EAST, receive);

        imagePanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                if (image != null)
                    g.drawImage(image, 0, 0, null);
            }
        };
        imagePanel.setBorder(new EtchedBorder());

        mainPanel.add(BorderLayout.SOUTH, southPanel);
        mainPanel.add(BorderLayout.CENTER, imagePanel);
        setContentPane(mainPanel);
        listeners();
        setPreferredSize(new Dimension(500, 500));
        pack();
        setLocationRelativeTo(mainPanel);
        setVisible(true);
        setResizable(true);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
    }

    private void listeners() {
        reference.addActionListener(e -> {
            ProcessBuilder pb = new ProcessBuilder("Notepad.exe", "src/Base/Reference.txt");
            try {
                pb.start();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        });
        receive.addActionListener(e -> {
            File image = chooseImage();
            if (image != null) {
                try {
                    this.image = ImageIO.read(image);
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
                try {
                    imageWay.setText(Imagination.starter(this.image));
                }
                catch (IOException e1) {
                }
                imagePanel.repaint();
            }
        });
    }

    private File chooseImage() {
        JFileChooser fileChooser = new JFileChooser();

        fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        fileChooser.setMultiSelectionEnabled(false);
        fileChooser.setFileFilter(new FileNameExtensionFilter("images", "png"));
        fileChooser.setDialogTitle("Выберите изображение");
        fileChooser.setCurrentDirectory(new File(".\\"));
        switch (fileChooser.showDialog(this, "Получить")) {
            case JFileChooser.APPROVE_OPTION:
                return fileChooser.getSelectedFile();
            case JFileChooser.ERROR_OPTION:
                System.exit(101);
            case JFileChooser.CANCEL_OPTION:
        }
        return null;
    }
}