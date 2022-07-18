package com.dugq.mybatisgenerator.generator3;

import com.dugq.exception.SqlException;
import com.dugq.mybatisgenerator.append.MyMergeShellCallback;
import com.dugq.mybatisgenerator.context.MyContext;
import com.dugq.mybatisgenerator.util.DbUtil;
import com.dugq.pojo.mybatis.AppendMapperConfigBean;
import com.dugq.pojo.mybatis.MySqlConfigBean;
import com.dugq.pojo.mybatis.TableConfigBean;
import com.dugq.service.config.impl.MybatisConfigService;
import com.intellij.ide.highlighter.JavaFileType;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiDirectory;
import com.intellij.psi.PsiElementFactory;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiFileFactory;
import com.intellij.psi.impl.file.PsiDirectoryFactory;
import org.jetbrains.annotations.NotNull;
import org.mybatis.generator.api.GeneratedJavaFile;
import org.mybatis.generator.api.GeneratedXmlFile;
import org.mybatis.generator.api.ProgressCallback;
import org.mybatis.generator.config.MergeConstants;
import org.mybatis.generator.config.TableConfiguration;
import org.mybatis.generator.exception.ShellException;
import org.mybatis.generator.internal.DefaultShellCallback;
import org.mybatis.generator.internal.NullProgressCallback;
import org.mybatis.generator.internal.XmlFileMergerJaxp;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static org.mybatis.generator.internal.util.messages.Messages.getString;

/**
 * Created by dugq on 2019-07-26.
 */
public class MyAppendMapperGenerator {
    private final Project project;
    private MybatisConfigService mybatisConfigService;
    private MyContext appendContext;
    private TableConfiguration tableConfiguration;
    private AppendMapperConfigBean appendMapperConfigBean;
    /**
     * The generated java files.
     */
    private List<GeneratedJavaFile> generatedJavaFiles;

    /**
     * The generated xml files.
     */
    private List<GeneratedXmlFile> generatedXmlFiles;

    public MyAppendMapperGenerator(Project project) {
        this.project = project;
        mybatisConfigService = project.getService(MybatisConfigService.class);
        generatedJavaFiles = new ArrayList<>();
        generatedXmlFiles = new ArrayList<>();
    }

    public void initBaseConfig() {
        generatedJavaFiles = new ArrayList<>();
        generatedXmlFiles = new ArrayList<>();
        final MySqlConfigBean mySqlConfigBean = mybatisConfigService.getAndFillIfEmpty();
        appendContext = MyContext.getAppendContextAndInitBaseConfig(mySqlConfigBean, project);
    }

    public List<String> getAllColumns() {
        if (Objects.isNull(this.appendContext) || Objects.isNull(this.tableConfiguration)) {
            throw new SqlException("数据库配置或者表配置为空");
        }
        return DbUtil.getAllColumns(this.appendContext, this.tableConfiguration);
    }

    public List<String> getAllIndexColumns() {
        if (Objects.isNull(this.appendContext) || Objects.isNull(this.tableConfiguration)) {
            throw new SqlException("数据库配置或者表配置为空");
        }
        return DbUtil.getIndexColumn(this.appendContext, this.tableConfiguration);
    }


    public void generator(AppendMapperConfigBean appendConfig) throws SQLException, InterruptedException, IOException {
        generatedJavaFiles.clear();
        generatedXmlFiles.clear();
        appendContext.setAppendConfig(appendConfig);
        appendContext.ready();
        doGenerator();
    }

    public void doGenerator() throws SQLException, InterruptedException, IOException {
        MyMergeShellCallback callback = new MyMergeShellCallback(project);
        ArrayList<String> warnings = new ArrayList<>();
        final NullProgressCallback progressCallback = new NullProgressCallback();
        appendContext.introspectTables(progressCallback, warnings,
                null);
        appendContext.generateFiles(progressCallback, generatedJavaFiles,
                generatedXmlFiles, warnings);
        for (GeneratedXmlFile gxf : generatedXmlFiles) {
            if (Objects.isNull(gxf)) {
                continue;
            }
            writeGeneratedXmlFile(gxf, progressCallback, callback, warnings);
        }

        try {
            ApplicationManager.getApplication().invokeLater( () -> {
                WriteCommandAction.runWriteCommandAction(project, () -> {
                    try {
                        writeAllJavaFile(callback, warnings, progressCallback);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void writeAllJavaFile(MyMergeShellCallback callback, ArrayList<String> warnings, NullProgressCallback progressCallback) throws InterruptedException, IOException {
        final List<GeneratedJavaFile> entityList = generatedJavaFiles.stream().filter(file -> file.getFileName().endsWith("Dto.java")
                || file.getFileName().endsWith("Param.java")
                || file.getFileName().endsWith("Entity.java")).collect(Collectors.toList());

        for (GeneratedJavaFile gjf :entityList) {
            if (Objects.isNull(gjf)) {
                continue;
            }
            writeGeneratedJavaFile(gjf, progressCallback, callback, warnings);
        }
        generatedJavaFiles.removeAll(entityList);
        for (GeneratedJavaFile gjf :generatedJavaFiles) {
            if (Objects.isNull(gjf)) {
                continue;
            }
            writeGeneratedJavaFile(gjf, progressCallback, callback, warnings);
        }
    }

    public void addTable(TableConfigBean config) {
        appendContext.initTableConfig(config);
        this.tableConfiguration = appendContext.getTableConfigurations().get(0);
    }


    private void writeGeneratedJavaFile(GeneratedJavaFile gjf, ProgressCallback callback, MyMergeShellCallback shellCallback, ArrayList<String> warnings)
            throws InterruptedException, IOException {
        PsiFile psiFile;
        try {
            final PsiDirectory directory = getPsiDirectory(gjf);
            PsiFile targetFile = directory.findFile(gjf.getFileName());
            if ( Objects.nonNull(targetFile)) {
                 shellCallback.myMergeJavaFile(gjf
                                    .getFormattedContent(), targetFile.getVirtualFile().getPath(),
                            MergeConstants.OLD_ELEMENT_TAGS,
                            gjf.getFileEncoding());
            } else {
                psiFile = buildPsiFile(gjf);
                directory.add(psiFile);
            }
            callback.checkCancel();
            callback.startTask(getString(
                    "Progress.15", gjf.getFileName())); //$NON-NLS-1$
        } catch (ShellException e) {
            warnings.add(e.getMessage());
        }
    }

    @NotNull
    private PsiDirectory getPsiDirectory(GeneratedJavaFile gjf) throws IOException {
        File file = new File(gjf.getTargetProject());
        if (!file.exists()){
            throw new SqlException("项目根目录找不到诶");
        }
        final VirtualFile root = LocalFileSystem.getInstance().findFileByIoFile(file);
        final String targetPackage = gjf.getTargetPackage();
        final String[] packageList = targetPackage.split("\\.");
        VirtualFile directoryFile = root;
        for (String pack :packageList){
            final VirtualFile child = directoryFile.findChild(pack);
            if (Objects.isNull(child) || !child.exists() || !child.isDirectory()){
                directoryFile = directoryFile.createChildDirectory(null, pack);
            }else{
                directoryFile = child;
            }
        }

        return PsiDirectoryFactory.getInstance(project).createDirectory(directoryFile);
    }

    private PsiFile buildPsiFile(GeneratedJavaFile genFile) {
        final String formattedContent = genFile.getFormattedContent();
        final PsiElementFactory psiElementFactory = PsiElementFactory.getInstance(project);
        final String fileName = genFile.getFileName();
        final PsiClass psiClass = psiElementFactory.createClass(fileName.split("\\.")[0]);
        return PsiFileFactory.getInstance(project).createFileFromText(genFile.getFileName(), JavaFileType.INSTANCE,formattedContent);
    }

    private void writeGeneratedXmlFile(GeneratedXmlFile gxf, ProgressCallback callback, DefaultShellCallback shellCallback, ArrayList<String> warnings)
            throws InterruptedException, IOException {
        File targetFile;
        String source;
        try {
            File directory = shellCallback.getDirectory(gxf
                    .getTargetProject(), gxf.getTargetPackage());
            targetFile = new File(directory, gxf.getFileName());
            if (targetFile.exists()) {
                if (gxf.isMergeable()) {
                    source = XmlFileMergerJaxp.getMergedSource(gxf,
                            targetFile);
                } else if (shellCallback.isOverwriteEnabled()) {
                    source = gxf.getFormattedContent();
                    warnings.add(getString("Warning.11", //$NON-NLS-1$
                            targetFile.getAbsolutePath()));
                } else {
                    source = gxf.getFormattedContent();
                    targetFile = getUniqueFileName(directory, gxf
                            .getFileName());
                    warnings.add(getString(
                            "Warning.2", targetFile.getAbsolutePath())); //$NON-NLS-1$
                }
            } else {
                source = gxf.getFormattedContent();
            }

            callback.checkCancel();
            callback.startTask(getString(
                    "Progress.15", targetFile.getName())); //$NON-NLS-1$
            writeFile(targetFile, source, "UTF-8"); //$NON-NLS-1$
        } catch (ShellException e) {
            warnings.add(e.getMessage());
        }
    }

    /**
     * Writes, or overwrites, the contents of the specified file.
     *
     * @param file         the file
     * @param content      the content
     * @param fileEncoding the file encoding
     * @throws IOException Signals that an I/O exception has occurred.
     */
    private void writeFile(File file, String content, String fileEncoding) throws IOException {
        FileOutputStream fos = new FileOutputStream(file, false);
        OutputStreamWriter osw;
        if (fileEncoding == null) {
            osw = new OutputStreamWriter(fos);
        } else {
            osw = new OutputStreamWriter(fos, fileEncoding);
        }

        BufferedWriter bw = new BufferedWriter(osw);
        bw.write(content);
        bw.close();
    }

    /**
     * Gets the unique file name.
     *
     * @param directory the directory
     * @param fileName  the file name
     * @return the unique file name
     */
    private File getUniqueFileName(File directory, String fileName) {
        File answer = null;

        // try up to 1000 times to generate a unique file name
        StringBuilder sb = new StringBuilder();
        for (int i = 1; i < 1000; i++) {
            sb.setLength(0);
            sb.append(fileName);
            sb.append('.');
            sb.append(i);

            File testFile = new File(directory, sb.toString());
            if (!testFile.exists()) {
                answer = testFile;
                break;
            }
        }

        if (answer == null) {
            throw new RuntimeException(getString(
                    "RuntimeError.3", directory.getAbsolutePath())); //$NON-NLS-1$
        }

        return answer;
    }
}
