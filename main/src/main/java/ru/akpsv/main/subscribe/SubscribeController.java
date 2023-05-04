package ru.akpsv.main.subscribe;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.akpsv.main.subscribe.model.Subscribe;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/subscribers")
public class SubscribeController {
    private final SubscribeService service;
    @PostMapping
    public Subscribe addSubscribe(@RequestParam Long subscriberId, @RequestParam Long publisherId){
        return service.addSubscriber(subscriberId, publisherId);
    }

    @GetMapping("/{subscriberId}")
    public List<Subscribe> getSubscribes(@PathVariable Long subscriberId){
        return service.getSubscribes(subscriberId);
    }

}
