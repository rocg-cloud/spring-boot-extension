package com.livk.ip2region.mvc.example.controller;

import com.livk.autoconfigure.ip2region.annotation.IP;
import com.livk.autoconfigure.ip2region.annotation.RequestIp;
import com.livk.autoconfigure.ip2region.doamin.IpInfo;
import com.livk.autoconfigure.ip2region.support.Ip2RegionSearch;
import com.livk.autoconfigure.ip2region.support.RequestIpContextHolder;
import com.livk.commons.bean.domain.Pair;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * <p>
 * Ip2RegionController
 * </p>
 *
 * @author livk
 */
@Slf4j
@Validated
@RestController
@RequestMapping("ip")
@RequiredArgsConstructor
public class Ip2RegionController {

    private final Ip2RegionSearch search;

    @GetMapping
    public HttpEntity<IpInfo> get(@IP String ip) {
        return ResponseEntity.ok(search.searchAsInfo(ip));
    }

    @PostMapping
    public HttpEntity<Pair<String, IpInfo>> post(@RequestIp String ip) {
        log.info("ip:{}", RequestIpContextHolder.get());
        return ResponseEntity.ok(Pair.of(ip, search.searchAsInfo(ip)));
    }
}