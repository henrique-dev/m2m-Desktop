/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.phdev.faciltransferencia.gui;

import br.com.phdev.faciltransferencia.connetion.OnClientFoundListener;
import br.com.phdev.faciltransferencia.connetion.OnConnectedListener;
import br.com.phdev.faciltransferencia.misc.Archive;
import br.com.phdev.faciltransferencia.misc.FTClient;
import br.com.phdev.faciltransferencia.misc.OnSendComplete;
import br.com.phdev.faciltransferencia.misc.TransferManager;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.dnd.DropTargetEvent;
import java.awt.dnd.DropTargetListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.TooManyListenersException;
import javax.imageio.ImageIO;
import javax.swing.AbstractListModel;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

/**
 *
 * @author Paulo Henrique Gonçalves Bacelar
 */
public class FTGui extends JFrame implements OnConnectedListener, OnSendComplete {

    private JMenuBar menu_bar;
    private JMenu menu_ft;
    private JMenu menu_help;

    private JList<String> list_clients;
    private JScrollPane scroll_list_clients;

    private JList<Archive> list_files;
    private JScrollPane scroll_list_files;

    private List<String> clients;
    private List<Archive> files;

    private TransferManager transferManager;

    public FTGui() {
        super("Facil Transferencia");
        super.setDefaultCloseOperation(EXIT_ON_CLOSE);
        super.setResizable(false);
        super.setAlwaysOnTop(true);
        super.setSize(600, 600);
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException e) {
        }
        super.setLayout(new BorderLayout(5, 5));

        this.transferManager = new TransferManager(this);
        this.transferManager.start();
        this.clients = new ArrayList<>();
        this.files = new ArrayList<>();

        this.initComponents();
        super.setLocationRelativeTo(null);
        super.setVisible(true);
    }

    private void initComponents() {
        this.menu_bar = new JMenuBar();
        this.menu_ft = new JMenu("FT");
        this.menu_bar.add(this.menu_ft);
        this.menu_help = new JMenu("Ajuda");
        this.menu_bar.add(this.menu_help);
        super.setJMenuBar(menu_bar);

        JPanel panel_clients = new JPanel();
        panel_clients.setLayout(new BorderLayout(5, 5));
        panel_clients.add(new JLabel("Dispositivos conectados: "), BorderLayout.PAGE_START);

        this.list_clients = new JList<>();
        updateClientList();

        panel_clients.add(this.scroll_list_clients, BorderLayout.CENTER);
        super.add(panel_clients, BorderLayout.LINE_START);

        this.list_files = new JList<>();
        this.list_files.setModel(new AbstractListModel<Archive>() {
            @Override
            public int getSize() {
                return FTGui.this.files.size();
            }

            @Override
            public Archive getElementAt(int index) {
                return FTGui.this.files.get(index);
            }
        });
        this.scroll_list_files = new JScrollPane();
        this.scroll_list_files.setViewportView(this.list_files);

        JPanel files_panel = new JPanel();
        files_panel.setLayout(new GridLayout(2, 1));
        files_panel.add(this.scroll_list_files);
        files_panel.add(new DropPane());
        super.add(files_panel, BorderLayout.CENTER);
    }

    private void updateClientList() {
        this.list_clients.setModel(new AbstractListModel<String>() {

            @Override
            public int getSize() {
                return FTGui.this.clients.size();
            }

            @Override
            public String getElementAt(int index) {
                return FTGui.this.clients.get(index);
            }
        });
        this.scroll_list_clients = new JScrollPane();
        this.scroll_list_clients.setViewportView(this.list_clients);
    }

    public static void main(String[] args) {
        new FTGui();
        //new TransferManager(new FTGui()).start();
    }

    @Override
    public void onConnected(String alias) {
        this.clients.add(alias);
        updateClientList();
    }

    @Override
    public void onComplete() {
        Archive[] tmp = new Archive[this.files.size()];
        for (int i = 0; i < tmp.length; i++) {
            tmp[i] = this.files.get(i);
        }
        this.list_files.setListData(tmp);
    }

    class DropPane extends JPanel {

        private DropTarget dropTarget;
        private DropTargetHandler dropTargetHandler;
        private Point dragPoint;

        private boolean dragOver = false;
        private BufferedImage target;

        private JLabel label_info;

        public DropPane() {
            super.setLayout(new GridBagLayout());
            this.label_info = new JLabel("Arraste os arquivos para cá");
            this.label_info.setFont(label_info.getFont().deriveFont(Font.BOLD, 24));
            super.add(label_info);
        }

        @Override
        public Dimension getPreferredSize() {
            return new Dimension(400, 400);
        }

        protected DropTarget getMyDropTarget() {
            if (dropTarget == null) {
                dropTarget = new DropTarget(this, DnDConstants.ACTION_COPY_OR_MOVE, null);
            }
            return dropTarget;
        }

        protected DropTargetHandler getDropTargetHandler() {
            if (dropTargetHandler == null) {
                dropTargetHandler = new DropTargetHandler();
            }
            return dropTargetHandler;
        }

        @Override
        public void addNotify() {
            super.addNotify();
            try {
                getMyDropTarget().addDropTargetListener(getDropTargetHandler());
            } catch (TooManyListenersException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void removeNotify() {
            super.removeNotify();
            getMyDropTarget().removeDropTargetListener(getDropTargetHandler());
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            if (dragOver) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setColor(new Color(0, 255, 0, 64));
                g2d.fill(new Rectangle(getWidth(), getHeight()));
                if (dragPoint != null && target != null) {
                    int x = dragPoint.x - 12;
                    int y = dragPoint.y - 12;
                    g2d.drawImage(target, x, y, this);
                }
                g2d.dispose();
            }
        }

        protected void importFiles(final List files) {
            Runnable run = new Runnable() {
                @Override
                public void run() {
                    try {
                        for (Object obj : files) {
                            Archive archive = new Archive();
                            archive.setName(((File) obj).getName());
                            archive.setPath(((File) obj).getPath());
                            FTGui.this.files.add(archive);
                            FTGui.this.list_files.updateUI();
                            /*
                            Archive[] tmp = new Archive[FTGui.this.files.size()];
                            for (int i = 0; i < tmp.length; i++) {
                                tmp[i] = FTGui.this.files.get(i);
                            }
                            FTGui.this.list_files.setListData(tmp);
                            */

                            FTGui.this.transferManager.addArchiveForTransfer(archive);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            };
            SwingUtilities.invokeLater(run);
        }

        protected class DropTargetHandler implements DropTargetListener {

            protected void processDrag(DropTargetDragEvent dtde) {
                if (dtde.isDataFlavorSupported(DataFlavor.javaFileListFlavor)) {
                    dtde.acceptDrag(DnDConstants.ACTION_COPY);
                } else {
                    dtde.rejectDrag();
                }
            }

            @Override
            public void dragEnter(DropTargetDragEvent dtde) {
                processDrag(dtde);
                SwingUtilities.invokeLater(new DragUpdate(true, dtde.getLocation()));
                repaint();
            }

            @Override
            public void dragOver(DropTargetDragEvent dtde) {
                processDrag(dtde);
                SwingUtilities.invokeLater(new DragUpdate(true, dtde.getLocation()));
                repaint();
            }

            @Override
            public void dropActionChanged(DropTargetDragEvent dtde) {

            }

            @Override
            public void dragExit(DropTargetEvent dte) {
                SwingUtilities.invokeLater(new DragUpdate(false, null));
                repaint();
            }

            @Override
            public void drop(DropTargetDropEvent dtde) {
                SwingUtilities.invokeLater(new DragUpdate(false, null));

                Transferable transferable = dtde.getTransferable();
                if (dtde.isDataFlavorSupported(DataFlavor.javaFileListFlavor)) {
                    dtde.acceptDrop(dtde.getDropAction());
                    try {
                        List transferData = (List) transferable.getTransferData(DataFlavor.javaFileListFlavor);
                        if (transferData != null && transferData.size() > 0) {
                            importFiles(transferData);
                            dtde.dropComplete(true);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    dtde.rejectDrop();
                }
            }

            public class DragUpdate implements Runnable {

                private boolean dragOver;
                private Point dragPoint;

                public DragUpdate(boolean dragOver, Point dragPoint) {
                    this.dragOver = dragOver;
                    this.dragPoint = dragPoint;
                }

                @Override
                public void run() {
                    DropPane.this.dragOver = dragOver;
                    DropPane.this.dragPoint = dragPoint;
                    DropPane.this.repaint();
                }

            }

        }

    }

}
