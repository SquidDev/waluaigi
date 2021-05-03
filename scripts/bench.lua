--[[
A very basic benchmark program. It doesn't get anywhere close to a real
workload, but allows us to have a quick look at comparative performance.
]]

local timeit = function(f)
    local iterations = 0
    if os.epoch then
        -- Support ComputerCraft if available.
        local t = os.epoch
        local start = t("utc")

        while t("utc") - start < 5000 do
            iterations = iterations + 1
            f()
        end
    else
        local t = os.time
        local start = t()

        while t() - start < 5 do
            iterations = iterations + 1
            f()
        end
    end

    return iterations
end

local iterations = timeit(function()
    -- We do some "work" (not really), largely to minimise os.time overhead
    -- as much as possible.
    local x = 0
    for i = 1, 1e5 do x = (x + 1) % 1024 end
end)

print(iterations)
