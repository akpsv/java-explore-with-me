package ru.akpsv.main.subscribe;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.akpsv.main.subscribe.dto.SubscribeDtoIn;
import ru.akpsv.main.subscribe.dto.SubscribeDtoOut;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/subscribes")
public class SubscribeController {
    private final SubscribeService subscribeService;
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public SubscribeDtoOut addSubscribe(@RequestBody SubscribeDtoIn subscribeDtoIn){
        return subscribeService.addSubscribe(subscribeDtoIn);
    }

    @GetMapping("/{subscriberId}")
    public List<SubscribeDtoOut> getPulblishersOfSubscriber(@PathVariable Long subscriberId){
        return subscribeService.getSubscribesOfSubscriber(subscriberId);
    }

    @DeleteMapping("/{subscribeId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteSubscribe(@PathVariable Long subscribeId){
        subscribeService.deleteSubscribe(subscribeId);
    }


}
