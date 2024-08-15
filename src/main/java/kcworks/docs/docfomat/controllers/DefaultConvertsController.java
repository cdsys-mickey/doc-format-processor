package kcworks.docs.docfomat.controllers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

/**
 * 只是為了相容舊版
 */
@Slf4j
@RequestMapping("/api/v1/format/converts")
@RestController
public class DefaultConvertsController extends JODConvertsController{

}
