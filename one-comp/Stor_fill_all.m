% Make a function out of this, that takes the officeIoT folder address and
% the number of runs as variables;

clear

OS1 = dlmread('officeIoT/RSMR1', ' ', 0, 1);
HS1 = dlmread('homeIoT/RSMR1', ' ', 0, 1);
BS1 = dlmread('buses/RSMR1', ' ', 0, 1);
PS1 = dlmread('non-proc_proc/RSMR1', ' ', 0, 1);


OS2 = dlmread('officeIoT/RSMR2', ' ', 0, 1);
HS2 = dlmread('homeIoT/RSMR2', ' ', 0, 1);
BS2 = dlmread('buses/RSMR2', ' ', 0, 1);
PS2 = dlmread('non-proc_proc/RSMR2', ' ', 0, 1);


OS3 = dlmread('officeIoT/RSMR3', ' ', 0, 1);
HS3 = dlmread('homeIoT/RSMR3', ' ', 0, 1);
BS3 = dlmread('buses/RSMR3', ' ', 0, 1);
PS3 = dlmread('non-proc_proc/RSMR3', ' ', 0, 1);


OS4 = dlmread('officeIoT/RSMR4', ' ', 0, 1);
HS4 = dlmread('homeIoT/RSMR4', ' ', 0, 1);
BS4 = dlmread('buses/RSMR4', ' ', 0, 1);
PS4 = dlmread('non-proc_proc/RSMR4', ' ', 0, 1);


OS5 = dlmread('officeIoT/RSMR5', ' ', 0, 1);
HS5 = dlmread('homeIoT/RSMR5', ' ', 0, 1);
BS5 = dlmread('buses/RSMR5', ' ', 0, 1);
PS5 = dlmread('non-proc_proc/RSMR5', ' ', 0, 1);


OS6 = dlmread('officeIoT/RSMR6', ' ', 0, 1);
HS6 = dlmread('homeIoT/RSMR6', ' ', 0, 1);
BS6 = dlmread('buses/RSMR6', ' ', 0, 1);
PS6 = dlmread('non-proc_proc/RSMR6', ' ', 0, 1);


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
ylabel('Storage used (B)','fontsize',12)
% xlim([17 48]);
set(bar_handle(1),'FaceColor',[0,0.5,1])
set(bar_handle(2),'FaceColor',[0,1,0])




figure

% subplot(2,1,1);
% yyaxis left
bar([mean(nonzeros(ostor_fill(:,1))), mean(nonzeros(ostor_fill(:,2))), mean(nonzeros(ostor_fill(:,3))), mean(nonzeros(ostor_fill(:,4))), mean(nonzeros(ostor_fill(:,5))), mean(nonzeros(ostor_fill(:,6)));
    mean(nonzeros(hstor_fill(:,1))), mean(nonzeros(hstor_fill(:,2))), mean(nonzeros(hstor_fill(:,3))), mean(nonzeros(hstor_fill(:,4))), mean(nonzeros(hstor_fill(:,5))), mean(nonzeros(hstor_fill(:,6)));
    mean(nonzeros(bstor_fill(:,1))), mean(nonzeros(bstor_fill(:,2))), mean(nonzeros(bstor_fill(:,3))), mean(nonzeros(bstor_fill(:,4))), mean(nonzeros(bstor_fill(:,5))), mean(nonzeros(bstor_fill(:,6)));
    mean(nonzeros(pstor_fill(:,1))), mean(nonzeros(pstor_fill(:,2))), mean(nonzeros(pstor_fill(:,3))), mean(nonzeros(pstor_fill(:,4))), mean(nonzeros(pstor_fill(:,5))), mean(nonzeros(pstor_fill(:,6)))]);
% title('Processing threads','fontsize',16)
xlabel('Scenario Number','fontsize',12) 
ylabel('Storage used (B)','fontsize',12)
%TODO:
% Modify this:
lgd1 =legend('1: f; 1.1; 100; 3:1 \newline2: f; 3; 100; 4:1; \newline3: t; 4; 100; 4:1; \newline4: t; 2; 100; 1:2', ...
            '1: f; 1.3; 100; 3:1 \newline2: f; 3.2; 100; 4:1; \newline3: t; 2.2; 500; 4:1;  \newline4: t; 2; 500; 2:2', ...
            '1: f; 2; 100; 3:1 \newline2: f; 50; 100; 4:1; \newline3: f; 3; 100 4:1; \newline4: f; 2; 100; 4:2', ...
            '1: f; 1.6; 300; 3:1 \newline2: f; 3; 200; 4:1; \newline3: f; 4; 300 4:1; \newline4: f; 2; 300; 5:2', ...
            '1: f; 2.2; 600; 3:1 \newline2: f; 3.2; 300; 4:1; \newline3: f; 2.2; 600 4:1; \newline4: f; 2; 600; 3:1', ...
            '1: f; 2; 900; 4:1 \newline2: f; 50; 600; 4:1; \newline3: f; 3; 900 4:1; \newline4: f; 2; 900; 4:1', 'Location', 'southoutside');
title(lgd1, 'Scenario Number: Storage Mode; Processing Delay; proc Freshness Period; proc Shelf-Life; non-proc Shelf-Life; Compress:Delete Ratio; Non-Proc:Proc Ratio');
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


