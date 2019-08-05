% Make a function out of this, that takes the officeIoT folder address and
% the number of runs as variables;

clear

OS1 = dlmread('officeIoT/RAMR1', ' ', 0, 1);
HS1 = dlmread('homeIoT/RAMR1', ' ', 0, 1);
BS1 = dlmread('buses/RAMR1', ' ', 0, 1);
PS1 = dlmread('non-proc_proc/RAMR1', ' ', 0, 1);


OS2 = dlmread('officeIoT/RAMR2', ' ', 0, 1);
HS2 = dlmread('homeIoT/RAMR2', ' ', 0, 1);
BS2 = dlmread('buses/RAMR2', ' ', 0, 1);
PS2 = dlmread('non-proc_proc/RAMR2', ' ', 0, 1);


OS3 = dlmread('officeIoT/RAMR3', ' ', 0, 1);
HS3 = dlmread('homeIoT/RAMR3', ' ', 0, 1);
BS3 = dlmread('buses/RAMR3', ' ', 0, 1);
PS3 = dlmread('non-proc_proc/RAMR3', ' ', 0, 1);


OS4 = dlmread('officeIoT/RAMR4', ' ', 0, 1);
HS4 = dlmread('homeIoT/RAMR4', ' ', 0, 1);
BS4 = dlmread('buses/RAMR4', ' ', 0, 1);
PS4 = dlmread('non-proc_proc/RAMR4', ' ', 0, 1);


OS5 = dlmread('officeIoT/RAMR5', ' ', 0, 1);
HS5 = dlmread('homeIoT/RAMR5', ' ', 0, 1);
BS5 = dlmread('buses/RAMR5', ' ', 0, 1);
PS5 = dlmread('non-proc_proc/RAMR5', ' ', 0, 1);


OS6 = dlmread('officeIoT/RAMR6', ' ', 0, 1);
HS6 = dlmread('homeIoT/RAMR6', ' ', 0, 1);
BS6 = dlmread('buses/RAMR6', ' ', 0, 1);
PS6 = dlmread('non-proc_proc/RAMR6', ' ', 0, 1);


[r3, c3] = size(OS1);

for repo = 1:c3
    ostor_fill(repo, 1) = mean(OS1(:, repo));
    if (isnan(mean(OS1(:, repo))))
        ostor_fill(repo, 1) = 0;
    end
    ostor_fill(repo, 2) = mean(OS2(:, repo));
    if (isnan(mean(OS2(:, repo))))
        ostor_fill(repo, 2) = 0;
    end
    ostor_fill(repo, 3) = mean(OS3(:, repo));
    if (isnan(mean(OS3(:, repo))))
        ostor_fill(repo, 3) = 0;
    end
    ostor_fill(repo, 4) = mean(OS4(:, repo));
    if (isnan(mean(OS4(:, repo))))
        ostor_fill(repo, 4) = 0;
    end
    ostor_fill(repo, 5) = mean(OS5(:, repo));
    if (isnan(mean(OS5(:, repo))))
        ostor_fill(repo, 5) = 0;
    end
    ostor_fill(repo, 6) =  mean(OS6(:, repo));
    if (isnan(mean(OS6(:, repo))))
        ostor_fill(repo, 6) = 0;
    end
    hstor_fill(repo, 1) = mean(HS1(:, repo));
    if (isnan(mean(HS1(:, repo))))
        hstor_fill(repo, 1) = 0;
    end
    hstor_fill(repo, 2) = mean(HS2(:, repo));
    if (isnan(mean(HS2(:, repo))))
        hstor_fill(repo, 2) = 0;
    end
    hstor_fill(repo, 3) = mean(HS3(:, repo));
    if (isnan(mean(HS3(:, repo))))
        hstor_fill(repo, 3) = 0;
    end
    hstor_fill(repo, 4) = mean(HS4(:, repo));
    if (isnan(mean(HS4(:, repo))))
        hstor_fill(repo, 4) = 0;
    end
    hstor_fill(repo, 5) = mean(HS5(:, repo));
    if (isnan(mean(HS5(:, repo))))
        hstor_fill(repo, 5) = 0;
    end
    hstor_fill(repo, 6) = mean(HS6(:, repo));
    if (isnan(mean(HS6(:, repo))))
        hstor_fill(repo, 6) = 0;
    end
    bstor_fill(repo, 1) = mean(BS1(:, repo));
    if (isnan(mean(BS1(:, repo))))
        bstor_fill(repo, 1) = 0;
    end
    bstor_fill(repo, 2) = mean(BS2(:, repo));
    if (isnan(mean(BS2(:, repo))))
        bstor_fill(repo, 2) = 0;
    end
    bstor_fill(repo, 3) = mean(BS3(:, repo));
    if (isnan(mean(BS3(:, repo))))
        bstor_fill(repo, 3) = 0;
    end
    bstor_fill(repo, 4) = mean(BS4(:, repo));
    if (isnan(mean(BS4(:, repo))))
        bstor_fill(repo, 4) = 0;
    end
    bstor_fill(repo, 5) = mean(BS5(:, repo));
    if (isnan(mean(BS5(:, repo))))
        bstor_fill(repo, 5) = 0;
    end
    bstor_fill(repo, 6) =  mean(BS6(:, repo));
    if (isnan(mean(BS6(:, repo))))
        bstor_fill(repo, 6) = 0;
    end
    pstor_fill(repo, 1) = mean(PS1(:, repo));
    if (isnan(mean(PS1(:, repo))))
        pstor_fill(repo, 1) = 0;
    end
    pstor_fill(repo, 2) = mean(PS2(:, repo));
    if (isnan(mean(PS2(:, repo))))
        pstor_fill(repo, 2) = 0;
    end
    pstor_fill(repo, 3) = mean(PS3(:, repo));
    if (isnan(mean(PS3(:, repo))))
        pstor_fill(repo, 3) = 0;
    end
    pstor_fill(repo, 4) = mean(PS4(:, repo));
    if (isnan(mean(PS4(:, repo))))
        pstor_fill(repo, 4) = 0;
    end
    pstor_fill(repo, 5) = mean(PS5(:, repo));
    if (isnan(mean(PS5(:, repo))))
        pstor_fill(repo, 5) = 0;
    end
    pstor_fill(repo, 6) =  mean(PS6(:, repo));
    if (isnan(mean(PS6(:, repo))))
        pstor_fill(repo, 6) = 0;
    end
end


figure

% subplot(2,1,1);
% yyaxis left
bar_handle = bar([ostor_fill(:,1), ostor_fill(:,2), ostor_fill(:,3), ostor_fill(:,4), ostor_fill(:,5), ostor_fill(:,6)]);
% title('Processing threads','fontsize',16)
xlabel('Repository number','fontsize',12) 
ylabel('Percentage (*100%) of messages processed within freshness period','fontsize',12)
% xlim([17 48]);
set(bar_handle(1),'FaceColor',[0,0.5,1])
set(bar_handle(2),'FaceColor',[0,1,0])




figure

% subplot(2,1,1);
% yyaxis left
bar([mean(ostor_fill(:,1)), mean(ostor_fill(:,2)), mean(ostor_fill(:,3)), mean(ostor_fill(:,4)), mean(ostor_fill(:,5)), mean(ostor_fill(:,6));
    mean(hstor_fill(:,1)), mean(hstor_fill(:,2)), mean(hstor_fill(:,3)), mean(hstor_fill(:,4)), mean(hstor_fill(:,5)), mean(hstor_fill(:,6));
    mean(bstor_fill(:,1)), mean(bstor_fill(:,2)), mean(bstor_fill(:,3)), mean(bstor_fill(:,4)), mean(bstor_fill(:,5)), mean(bstor_fill(:,6));
    mean(pstor_fill(:,1)), mean(pstor_fill(:,2)), mean(pstor_fill(:,3)), mean(pstor_fill(:,4)), mean(pstor_fill(:,5)), mean(pstor_fill(:,6))]);
% title('Processing threads','fontsize',16)
xlabel('Scenario Number','fontsize',12) 
ylabel('Percentage (*100%) of messages processed within freshness period','fontsize',12)
%TODO:
% Modify this:
lgd1 =legend('1: 1; 1.1; 100; 3,1 \newline2: 0.2; 3; 3; 100; \newline3: 0.3; 3; 4; 100 \newline4: 100; 4.1', '1: 1; 1.1; 100; 3,1 \newline2: 0.2; 3; 3; 100; \newline3: 0.3; 3; 4; 100 \newline4: 100; 3:1', '1: 1; 1.1; 100; 3,1 \newline2: 0.2; 3; 3; 100; \newline3: 0.3; 3; 4; 100 \newline4: 100; 5:2', '1: 1; 1.1; 100; 3,1 \newline2: 0.2; 3; 3; 100; \newline3: 0.3; 3; 4; 100 \newline4: 100; 4:2', '1: 1; 1.1; 100; 3,1 \newline2: 0.2; 3; 3; 100; \newline3: 0.3; 3; 4; 100 \newline4: 100; 2:2', '1: 1; 1.1; 100; 3,1 \newline2: 0.2; 3; 3; 100; \newline3: 0.3; 3; 4; 100 \newline4: 100; 2:1', 'Location', 'southoutside');
title(lgd1, 'Non-Proc:Proc');
lgd1.NumColumns = 6;
% xlim([17 48]);
% set(bar_handle(1),'FaceColor',[0,0.5,1])
% set(bar_handle(2),'FaceColor',[0,1,0])


% procApp2.delay = 0.1
% procApp2.freshPer = [1; 1; 1; 1.5; 2; 3; 1]
% procApp2.procShelf = [1.1; 1.3; 2; 1.6; 2.2; 3.3; 2]
% procApp2.nonprocShelf = [100; 100; 100; 200; 300; 600]
% procApp2.destinationName = r
% procApp2.procSize = 50k
% # Stored message ratio to processing message (no. of messages to be stored vs. no. of messages to be processed)
% procApp2.compressRate = 1,4
% procApp2.passiveRate = [3,1; 3,1; 3,1; 3,1; 3,1; 4,1]


% procApp2.delay = [0.2; 0.2; 0.2; 0.1; 0.1; 0.1]
% procApp2.freshPer = [3; 3; 3; 2; 2; 2]
% procApp2.procShelf = [3; 3.2; 50]
% procApp2.nonprocShelf = [100; 100; 100; 200; 300; 600]
% procApp2.destinationName = r
% procApp2.procSize = 1M
% # Stored message ratio to processing message (no. of messages to be stored vs. no. of messages to be processed)
% procApp2.compressRate = [2,1; 2,1; 2,2; 2,2; 1,2; 1,2]
% procApp2.passiveRate = 4,1


% procApp.storageMode = [true; true; false; false; false; false]
% procApp.maxStorTime = [3000; 3000]
% 
% 
% procApp3.interval = 0.7
% procApp3.delay = [0.3; 0.3; 0.3; 0.3; 0.1; 0.1]
% procApp3.freshPer = [3; 2; 2]
% procApp3.procShelf = [4; 2.2; 3]
% procApp3.nonprocShelf = [100; 500; 100; 300; 600; 900]
% procApp3.destinationName = r
% procApp3.procSize = 1M
% # Stored message ratio to processing message (no. of messages to be stored vs. no. of messages to be processed)
% procApp3.compressRate = 3,1
% procApp3.passiveRate = [4,1]
% procApp3.passive = false
% 
% 
% procApp2.delay = 0.1
% procApp2.freshPer = 1
% procApp2.procShelf = 2
% procApp3.nonprocShelf = [100; 500; 100; 300; 600; 900]
% procApp2.destinationName = r
% procApp2.procSize = 500k
% # Stored message ratio to processing message (no. of messages to be stored vs. no. of messages to be processed)
% procApp2.compressRate = 2,2
% procApp2.passiveRate = [4,1; 3,1; 5,2; 4,2; 2,2; 1,2]
% procApp2.passive = false


