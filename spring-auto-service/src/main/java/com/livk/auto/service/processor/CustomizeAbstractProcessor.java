/*
 * Copyright 2021 spring-boot-extension the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package com.livk.auto.service.processor;

import com.google.common.collect.ListMultimap;

import javax.annotation.processing.*;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import javax.tools.FileObject;
import javax.tools.StandardLocation;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * <p>
 * CustomizeAbstractProcessor
 * </p>
 *
 * @author livk
 */
abstract class CustomizeAbstractProcessor extends AbstractProcessor {

    /**
     * The Filer.
     */
    protected Filer filer;

    /**
     * The Elements.
     */
    protected Elements elements;

    /**
     * The Messager.
     */
    protected Messager messager;

    /**
     * The Options.
     */
    protected Map<String, String> options;

    /**
     * The Types.
     */
    protected Types types;

    /**
     * The Out.
     */
    protected StandardLocation out;

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        out = StandardLocation.CLASS_OUTPUT;
        filer = processingEnv.getFiler();
        elements = processingEnv.getElementUtils();
        messager = processingEnv.getMessager();
        options = processingEnv.getOptions();
        types = processingEnv.getTypeUtils();
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latest();
    }

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        return getSupportedAnnotation().stream()
                .map(Class::getName)
                .collect(Collectors.toSet());
    }

    /**
     * Set supported annotations
     *
     * @return Set class
     */
    protected abstract Set<Class<?>> getSupportedAnnotation();

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        if (roundEnv.processingOver()) {
            generateConfigFiles();
        } else {
            processAnnotations(annotations, roundEnv);
        }
        return false;
    }

    /**
     * 生成文件
     */
    protected abstract void generateConfigFiles();

    /**
     * 处理注解
     *
     * @param annotations annotations
     * @param roundEnv    roundEnv
     */
    protected abstract void processAnnotations(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv);


    /**
     * Gets annotation mirror attributes.
     *
     * @param <T>         the type parameter
     * @param element     the element
     * @param targetClass the target class
     * @param key         the key
     * @return the annotation mirror attributes
     */
    protected <T> Optional<String> getAnnotationMirrorAttributes(Element element, Class<T> targetClass, String key) {
        for (AnnotationMirror annotationMirror : element.getAnnotationMirrors()) {
            TypeElement typeElement = (TypeElement) annotationMirror.getAnnotationType().asElement();
            if (typeElement.getQualifiedName().contentEquals(targetClass.getCanonicalName())) {
                Map<String, String> map = annotationMirror.getElementValues()
                        .entrySet()
                        .stream()
                        .filter(entry -> entry.getValue() != null)
                        .collect(Collectors.toMap(entry -> entry.getKey().getSimpleName().toString(),
                                entry -> entry.getValue().getValue().toString()));
                return Optional.ofNullable(map.get(key));
            }
        }
        return Optional.empty();
    }

    /**
     * Factories add.
     *
     * @param factoriesMap the factories map
     * @param provider     the provider
     * @param serviceImpl  the service
     */
    protected void factoriesAdd(ListMultimap<String, String> factoriesMap, String provider, String serviceImpl) {
        if (!factoriesMap.containsEntry(provider,serviceImpl)) {
            factoriesMap.put(provider, serviceImpl);
        }
    }

    /**
     * 类字符串进行转换
     *
     * @param provider the provider
     * @return the string
     */
    protected String transform(String provider) {
        char[] providerCharArray = provider.toCharArray();
        boolean flag = false;
        for (int i = 0; i < providerCharArray.length - 1; i++) {
            if (providerCharArray[i] == '.'){
                if (flag){
                    providerCharArray[i] = '$';
                }else if(providerCharArray[i + 1] >= 'A' && providerCharArray[i + 1] <= 'Z'){
                    flag = true;
                }
            }
        }
        return new String(providerCharArray);
    }

    /**
     * buffered reader.
     *
     * @param fileObject the file object
     * @return the buffered reader
     * @throws IOException the io exception
     */
    protected BufferedReader bufferedReader(FileObject fileObject) throws IOException {
        return new BufferedReader(fileObject.openReader(true));
    }

    /**
     * buffered writer.
     *
     * @param fileObject the file object
     * @return the buffered writer
     * @throws IOException the io exception
     */
    protected BufferedWriter bufferedWriter(FileObject fileObject) throws IOException {
        return new BufferedWriter(fileObject.openWriter());
    }
}
